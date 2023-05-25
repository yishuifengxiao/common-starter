/**
 *
 */
package com.yishuifengxiao.common.web;

import ch.qos.logback.classic.Level;
import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.log.LogLevelUtil;
import com.yishuifengxiao.common.tool.random.UID;
import com.yishuifengxiao.common.tool.utils.SystemUtil;
import com.yishuifengxiao.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
@ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class WebEnhanceAutoConfiguration {

    @Autowired
    private WebEnhanceProperties webEnhanceProperties;

    /**
     * 注入一个跨域支持过滤器
     *
     * @return 跨域支持过滤器
     */
    @Bean("corsAllowedFilter")
    @ConditionalOnMissingBean(name = "corsAllowedFilter")
    @ConditionalOnProperty(prefix = "yishuifengxiao.web.cors", name = {"enable"}, havingValue = "true",
            matchIfMissing = true)
    public FilterRegistrationBean<CustomCorsFilter> corsAllowedFilter() {
        CustomCorsFilter corsFilter = new CustomCorsFilter(webEnhanceProperties.getCors());
        FilterRegistrationBean<CustomCorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
        registration.setName("corsAllowedFilter");
        registration.setUrlPatterns(webEnhanceProperties.getCors().getUrlPatterns());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }


    /**
     * 请求跟踪拦截器用于增加一个请求追踪标志
     *
     * @return
     */
    @Bean("requestTrackingFilter")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(name = "requestTrackingFilter")
    @ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"tracked"}, matchIfMissing = true)
    public Filter requestTrackingFilter() {
        return new TracedFilter(webEnhanceProperties);
    }


    /**
     * 全局参数校验功能自动配置
     *
     * @author yishui
     * @version 1.0.0
     * @since 1.0.0
     */
    @Configuration
    @Aspect
    @ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"aop"}, havingValue = "true", matchIfMissing = true)
    class ValidAutoConfiguration {

        /**
         * 定义切入点
         */
        @Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody) || @annotation(com" +
                ".yishuifengxiao.common.web.annotation.DataValid)")
        public void pointCut() {
        }

        /**
         * 执行环绕通知
         *
         * @param joinPoint ProceedingJoinPoint
         * @return 请求响应结果
         * @throws Throwable 处理时发生异常
         */
        @Around("pointCut()")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            // 获取所有的请求参数
            Object[] args = joinPoint.getArgs();
            if (null != args && args.length > 0) {
                for (Object obj : args) {
                    if (obj instanceof BindingResult) {
                        BindingResult errors = (BindingResult) obj;
                        if (errors.hasErrors()) {
                            return Response.badParam(errors.getFieldErrors().get(0).getDefaultMessage());
                        }
                        break;
                    }
                }
            }
            return joinPoint.proceed();

        }

        @PostConstruct
        public void checkConfig() {

            log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局参数校验功能> 相关的配置");
        }

    }

    @SuppressWarnings("rawtypes")
    @ControllerAdvice
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(DispatcherServlet.class)
    @ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"tracked"}, matchIfMissing = true)
    class WebResponseBodyAutoConfiguration implements ResponseBodyAdvice {


        @Override
        public boolean supports(MethodParameter returnType, Class converterType) {
            return returnType.hasMethodAnnotation(ResponseBody.class);
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                      Class selectedConverterType, ServerHttpRequest request,
                                      ServerHttpResponse response) {
            try {
                if (null != body && body instanceof Response) {
                    Response result = (Response) body;
                    Object attribute = null;
                    if (request instanceof ServletServerHttpRequest) {
                        HttpServletRequest httpServerHttpRequest =
                                ((ServletServerHttpRequest) request).getServletRequest();
                        attribute = httpServerHttpRequest.getAttribute(webEnhanceProperties.getTracked());
                    }
                    if (null == attribute || StringUtils.isBlank(attribute.toString())) {
                        attribute = TraceContext.get();
                    }
                    if (null != attribute) {
                        result.setId(attribute.toString());
                    }
                    return result;
                }
            } catch (Exception e) {
                log.debug("【yishuifengxiao-common-spring-boot-starter】:There was a problem obtaining the request " +
                        "tracking id {}", e);
            }

            return body;
        }


        /**
         * 配置检查
         */
        @PostConstruct
        public void checkConfig() {

            log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <响应增强功能> 相关的配置");
        }
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
                                        FilterChain filterChain) throws ServletException, IOException {
            try {
                try {
                    String ssid = UID.uuid();
                    request.setAttribute(webEnhanceProperties.getTracked(), ssid);
                    TraceContext.set(ssid);
                    // 动态设置日志
                    String dynamicLogLevel = webEnhanceProperties.getDynamicLogLevel();
                    if (StringUtils.isNotBlank(dynamicLogLevel) && !StringUtils.equalsIgnoreCase("false",
                            dynamicLogLevel)) {
                        // 开启动态日志功能
                        String[] tokens = dynamicLogLevel(request.getHeader(webEnhanceProperties.getDynamicLogLevel()));
                        if (null == tokens) {
                            dynamicLogLevel(request.getParameter(webEnhanceProperties.getDynamicLogLevel()));
                        }
                        if (null != tokens) {
                            LogLevelUtil.setLevel(tokens[0], tokens[1]);
                        }
                    }
                } catch (Exception e) {
                    log.debug("【yishuifengxiao-common-spring-boot-starter】:There was a problem when setting the " +
                            "tracking log and dynamic modification log level. The problem is {}", e);
                }

                filterChain.doFilter(request, response);
            } finally {
                TraceContext.clear();
            }


        }

        /**
         * 解析动态日志功能参数
         *
         * @param text 待解析的文本
         * @return 解析出来的数据
         */
        private String[] dynamicLogLevel(String text) {
            String[] tokens = StringUtils.splitByWholeSeparator(text, SystemUtil.COLON);
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
            return tokens;
        }

    }

    /**
     * 自定义跨域支持
     *
     * @author yishui
     * @version 1.0.0
     * @since 1.0.0
     */
    static class CustomCorsFilter implements Filter {

        private WebEnhanceProperties.CorsProperties corsProperties;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                ServletException {

            try {
                if (response instanceof HttpServletResponse) {

                    HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

                    //Access-Control-Allow-Origin
                    String accessControlAllowOrigin = HttpUtils.accessControlAllowOrigin((HttpServletRequest) request);
                    //controlAllowHeaders
                    String controlAllowHeaders = HttpUtils.accessControlAllowHeaders((HttpServletRequest) request,
                            httpServletResponse);

                    httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                            StringUtils.isBlank(corsProperties.getAllowedOrigins()) ? accessControlAllowOrigin :
                                    corsProperties.getAllowedOrigins());

                    httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                            StringUtils.isBlank(corsProperties.getAllowedHeaders()) ? controlAllowHeaders :
                                    corsProperties.getAllowedHeaders());

                    httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                            corsProperties.getAllowCredentials() + "");

                    httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                            corsProperties.getAllowedMethods());
                    corsProperties.getHeaders().forEach((k, v) -> {
                        if (StringUtils.isNoneBlank(k, v)) {
                            httpServletResponse.setHeader(k, v);
                        }
                    });
                }

            } catch (Throwable e) {
                if (log.isInfoEnabled()) {
                    log.info("[unkown] 跨域支持捕获到未知异常 {}", e.getMessage());
                }

            }

            chain.doFilter(request, response);
        }

        public CustomCorsFilter(WebEnhanceProperties.CorsProperties corsProperties) {
            this.corsProperties = corsProperties;
        }

    }


}
