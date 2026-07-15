/**
 *
 */
package com.yishuifengxiao.common.web;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.log.LogLevelUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.OsUtils;
import com.yishuifengxiao.common.utils.HttpUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * web增强支持配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({WebEnhanceProperties.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"enable"}, havingValue = "true")
public class WebEnhanceAutoConfiguration {

    @Autowired
    private WebEnhanceProperties webEnhanceProperties;
    @Autowired(required = false)
    private Validator validator;
    @Autowired(required = false)
    private ObjectMapper objectMapper;

    /**
     * 创建并配置参数验证切面(ParamValidationAspect)的Bean
     * 该切面用于处理方法参数的验证逻辑
     *
     * @return 配置好的ParamValidationAspect实例
     */
    @Bean
    public ParamValidationAspect paramValidationAspect() {
        // 使用validator和webEnhanceProperties创建参数验证切面实例
        return new ParamValidationAspect(validator, webEnhanceProperties,objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(ResponseWrapAdvice.class)
    public ResponseWrapAdvice responseWrapAdvice() {
        return new ResponseWrapAdvice(webEnhanceProperties);
    }

    /**
     * 注入一个跨域支持过滤器
     *
     * @return 跨域支持过滤器
     */
    @Bean("corsAllowedFilter")
    @ConditionalOnMissingBean(name = "corsAllowedFilter")
    @ConditionalOnProperty(prefix = "yishuifengxiao.web.cors", name = {"enable"}, havingValue =
            "true", matchIfMissing = true)
    public FilterRegistrationBean<CustomCorsFilter> corsAllowedFilter() {
        CustomCorsFilter corsFilter = new CustomCorsFilter(webEnhanceProperties.getCors());
        FilterRegistrationBean<CustomCorsFilter> registration =
                new FilterRegistrationBean<>(corsFilter);
        registration.setName("corsAllowedFilter");
        registration.setUrlPatterns(webEnhanceProperties.getCors().getUrlPatterns());
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }


    /**
     * 请求跟踪拦截器用于增加一个请求追踪标志
     *
     * @return
     */
    @Bean("requestTrackingFilter")
    @Order(Ordered.HIGHEST_PRECEDENCE + 1000)
    @ConditionalOnMissingBean(name = "requestTrackingFilter")
    @ConditionalOnProperty(prefix = "yishuifengxiao.web.traced", name = {"enable"}, havingValue =
            "true", matchIfMissing = true)
    public FilterRegistrationBean<TracedFilter> requestTrackingFilter() {
        TracedFilter tracedFilter = new TracedFilter(webEnhanceProperties);
        FilterRegistrationBean<TracedFilter> registration =
                new FilterRegistrationBean<>(tracedFilter);
        registration.setName("requestTrackingFilter");
        registration.setUrlPatterns(Arrays.asList("/*"));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
        return registration;
    }


    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {
        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <web增强支持> 相关的配置");

    }


    /**
     * 追踪过滤器
     */
    static class TracedFilter extends OncePerRequestFilter {

        private WebEnhanceProperties webEnhanceProperties;

        public TracedFilter(WebEnhanceProperties webEnhanceProperties) {
            this.webEnhanceProperties = webEnhanceProperties;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException,
                IOException {
            String ssid = IdWorker.snowflakeStringId();
            TraceContext.set(ssid);
            MDC.put(webEnhanceProperties.getTraced().getTrackedId(), ssid);
            try {
                // 动态设置日志
                String dynamicLogLevel = webEnhanceProperties.getTraced().getDynamicLogLevel();
                if (StringUtils.isNotBlank(dynamicLogLevel) && !StringUtils.equalsIgnoreCase(
                        "false", dynamicLogLevel)) {
                    // 开启动态日志功能
                    String[] tokens = dynamicLogLevel(request.getHeader(dynamicLogLevel.trim()));
                    if (null != tokens) {
                        LogLevelUtil.setLevel(tokens[0], tokens[1]);
                    }
                }
                filterChain.doFilter(request, response);
            } finally {
                TraceContext.clear();
                MDC.remove(webEnhanceProperties.getTraced().getTrackedId());
            }


        }

        /**
         * 解析动态日志功能参数
         *
         * @param text 待解析的文本
         * @return 解析出来的数据
         */
        private String[] dynamicLogLevel(String text) {
            if (StringUtils.isBlank(text)) {
                return null;
            }
            try {
                String val =
                        new String(Base64.getDecoder().decode(text.getBytes(StandardCharsets.UTF_8)),
                                StandardCharsets.UTF_8);
                if (StringUtils.isBlank(val)) {
                    return null;
                }
                String[] tokens = StringUtils.splitByWholeSeparator(val, OsUtils.COLON);
                if (null == tokens || tokens.length != 2) {
                    return null;
                }
                if (StringUtils.isBlank(tokens[0]) || StringUtils.isBlank(tokens[1])) {
                    return null;
                }
                Level level = Level.toLevel(tokens[1].trim(), null);
                if (null == level) {
                    return null;
                }
                return new String[]{tokens[0], level.levelStr};
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("【yishuifengxiao-common-spring-boot-starter】: 动态日志级别解析失败 {}", e.getMessage());
                }
            }
            return null;

        }

    }

    /**
     * 自定义跨域支持
     *
     * @author yishui
     * @version 1.0.0
     * @since 1.0.0
     */
    static class CustomCorsFilter extends OncePerRequestFilter {

        private WebEnhanceProperties.CorsProperties corsProperties;


        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException,
                IOException {
            try {
                corsProperties.getHeaders().forEach((k, v) -> {
                    if (StringUtils.isNoneBlank(k, v)) {
                        response.setHeader(k.trim(), v.trim());
                    }
                });
                //Vary是一个HTTP响应头，用于指定缓存服务器如何处理不同的客户端请求。当客户端发送一个包含某些请求头信息的请求时，
                // 缓存服务器会检查该请求头信息是否与缓存中的响应匹配。如果匹配成功，则缓存服务器可以直接返回缓存中的响应，而无需向原始服务器发送请求。
                //例如，如果缓存服务器收到一个带有Accept-Encoding:gzip请求头信息的请求，它将检查缓存中是否存在与该请求头信息匹配的响应。
                // 如果存在，则缓存服务器可以直接返回缓存中的压缩响应，而无需向原始服务器发送请求。
                Collection<String> varyHeaders = response.getHeaders(HttpHeaders.VARY);
                if (!varyHeaders.contains(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)) {
                    response.addHeader(HttpHeaders.VARY, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                }
                if (!varyHeaders.contains(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD)) {
                    response.addHeader(HttpHeaders.VARY, HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
                }

                //Access-Control-Allow-Origin
                String accessControlAllowOrigin = HttpUtils.accessControlAllowOrigin(request);
                //controlAllowHeaders
                accessControlAllowOrigin = Arrays.asList(accessControlAllowOrigin,
                        corsProperties.getAllowedOrigins()).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));

                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        StringUtils.isBlank(accessControlAllowOrigin) ? "*" :
                                accessControlAllowOrigin);

                String accessControlAllowHeaders =
                        StringUtils.isBlank(corsProperties.getAllowedHeaders()) ?
                                HttpUtils.accessControlAllowHeaders(request, response) :
                                corsProperties.getAllowedHeaders();

                //  Access-Control-Allow-Headers是一个HTTP响应头，用于指定客户端可以在预检请求中使用哪些HTTP
                //  请求头信息。预检请求是浏览器在发送跨域请求之前发送的一种OPTIONS
                //  请求，用于检测实际请求是否安全。在预检请求中，浏览器会向服务端发送一些额外的请求头信息，例如Authorization、Content-Type
                //  等，以检查服务端是否允许这些请求头信息。
                // 如果服务端不允许某些请求头信息，浏览器将会收到一个错误响应。为了避免这种情况，您可以在服务端的HTTP响应头中添加Access-Control-Allow
                // -Headers
                // 头信息，以允许客户端使用指定的HTTP请求头信息。例如
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        accessControlAllowHeaders);
                //  Access-Control-Expose-Headers是一个HTTP响应头，用于指定哪些HTTP响应头信息可以被客户端访问。
                //  默认情况下，客户端只能访问以下响应头信息 ：
                //  Cache-Control 、Content-Language 、Content-Type、Expires、Last-Modified、Pragma
                //  如果您的服务端在响应头中添加了自定义的HTTP响应头信息，例如Authorization，客户端将无法访问该响应头信息
                //  此时，您可以在服务端的HTTP响应头中添加Access-Control-Expose-Headers头信息，以允许客户端访问指定的HTTP响应头信息。
                response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        accessControlAllowHeaders);

                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                        corsProperties.getAllowCredentials() + "");

                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        corsProperties.getAllowedMethods());

            } catch (Throwable e) {
                if (log.isInfoEnabled()) {
                    log.info("[unknown] 跨域支持捕获到未知异常 {}", e.getMessage());
                }
            }
            filterChain.doFilter(request, response);
        }

        public CustomCorsFilter(WebEnhanceProperties.CorsProperties corsProperties) {
            this.corsProperties = corsProperties;
        }

    }


}
