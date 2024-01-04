/**
 *
 */
package com.yishuifengxiao.common.web;

import ch.qos.logback.classic.Level;
import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.IllegalParameterException;
import com.yishuifengxiao.common.tool.log.LogLevelUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.OsUtils;
import com.yishuifengxiao.common.tool.validate.BeanValidator;
import com.yishuifengxiao.common.utils.HttpUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
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
    @ConditionalOnProperty(prefix = "yishuifengxiao.web.cors", name = {"enable"}, havingValue = "true", matchIfMissing = true)
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

        @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
        public void controllerClass() {
        }

        @Pointcut("execution(public * *(..))")
        public void publicMethod() {
        }

        @Pointcut("execution(public * *(.., @org.springframework.validation.annotation.Validated (*), ..))")
        public void validatedParam() {
        }

        @Pointcut("execution(public * *(.., @javax.validation.Valid (*), ..))")
        public void validParam() {
        }

        /**
         * 拦截被@Controller或@RestController注解的类中修饰符为public的方法，
         * 且方法包含被@Validated修饰的参数或者方法包含BindingResult参数
         *
         * @param joinPoint ProceedingJoinPoint
         * @return 请求响应结果
         * @throws Throwable 处理时发生异常
         */
        @Around("controllerClass() && publicMethod() && (validatedParam() || validParam() || args(.., org.springframework.validation.BindingResult))")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            // 获取所有的请求参数
            Object[] args = joinPoint.getArgs();
            if (null == args || args.length == 0) {
                return joinPoint.proceed();
            }
            // 判断系统中是否使用了 BindingResult 进行接收
            BindingResult errors = (BindingResult) Arrays.asList(args).stream().filter(arg -> arg instanceof BindingResult).findFirst().orElse(null);
            if (null != errors) {
                // 已使用 BindingResult 收集
                if (errors.hasErrors()) {
                    throw new IllegalParameterException(errors.getFieldErrors().get(0).getDefaultMessage());
                }
            } else {
                // 未使用 BindingResult 收集
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                Method method = methodSignature.getMethod();
                // 遍历方法的参数
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Annotation[] annotations = parameter.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().equals(Valid.class)) {
                            // 参数被@Valid注解修饰
                            String msg = BeanValidator.validateResult(args[i]);
                            if (null != msg) {
                                throw new IllegalParameterException(msg);
                            }
                            break;
                        } else if (annotation.annotationType().equals(Validated.class)) {

                            Validated validated = (Validated) annotation;
                            // 获取参数上的@Validated注解的值
                            Class<?>[] validatedGroups = validated.value();
                            if (null != validatedGroups && validatedGroups.length > 0) {
                                // 进行校验的逻辑...
                                for (Class<?> validatedGroup : validatedGroups) {
                                    String msg = BeanValidator.validateResult(args[i], validatedGroup);
                                    if (null != msg) {
                                        throw new IllegalParameterException(msg);
                                    }
                                }
                            } else {
                                String msg = BeanValidator.validateResult(args[i]);
                                if (null != msg) {
                                    throw new IllegalParameterException(msg);
                                }
                            }
                            break;
                        }
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
    @ConditionalOnProperty(prefix = "yishuifengxiao.web.response", name = {"enable"}, havingValue = "true")
    class WebResponseBodyAutoConfiguration implements ResponseBodyAdvice {


        @Override
        public boolean supports(MethodParameter returnType, Class converterType) {
            String className = returnType.getDeclaringClass().getName();
            boolean anyMatch = webEnhanceProperties.getResponse().getExcludes().stream().anyMatch(v -> StringUtils.equalsIgnoreCase(v, className));
            if (anyMatch) {
                return false;
            }

            return returnType.hasMethodAnnotation(ResponseBody.class) || null != returnType.getDeclaringClass().getDeclaredAnnotation(RestController.class);
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            try {
                if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(selectedContentType)) {
                    //开启全局响应数据格式统一
                    Response<Object> result = body instanceof Response ? (Response) body : Response.sucData(body);
                    result.setId(getTracked(request));
                    return result;
                } else {
                    if (null != body && body instanceof Response) {
                        Response result = (Response) body;
                        result.setId(getTracked(request));
                        return result;
                    }
                }

            } catch (Exception e) {
                log.debug("【yishuifengxiao-common-spring-boot-starter】:There was a problem obtaining the request " + "tracking id {}", e);
            }

            return body;
        }

        private String getTracked(ServerHttpRequest request) {
            Object attribute = null;
            if (request instanceof ServletServerHttpRequest) {
                HttpServletRequest httpServerHttpRequest = ((ServletServerHttpRequest) request).getServletRequest();
                attribute = httpServerHttpRequest.getAttribute(webEnhanceProperties.getTracked());
            }
            if (null == attribute || StringUtils.isBlank(attribute.toString())) {
                attribute = TraceContext.get();
            }
            return null != attribute ? attribute.toString() : null;
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
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            try {
                try {
                    String ssid = IdWorker.uuid();
                    request.setAttribute(webEnhanceProperties.getTracked(), ssid);
                    TraceContext.set(ssid);
                    // 动态设置日志
                    String dynamicLogLevel = webEnhanceProperties.getDynamicLogLevel();
                    if (StringUtils.isNotBlank(dynamicLogLevel) && !StringUtils.equalsIgnoreCase("false", dynamicLogLevel)) {
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
                    log.debug("【yishuifengxiao-common-spring-boot-starter】:There was a problem when setting the " + "tracking log and dynamic modification log level. The problem is {}", e);
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
            String[] tokens = StringUtils.splitByWholeSeparator(text, OsUtils.COLON);
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
    static class CustomCorsFilter extends OncePerRequestFilter {

        private WebEnhanceProperties.CorsProperties corsProperties;


        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
                accessControlAllowOrigin = Arrays.asList(accessControlAllowOrigin, corsProperties.getAllowedOrigins()).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));


                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, StringUtils.isBlank(accessControlAllowOrigin) ? "*" : accessControlAllowOrigin);

                String accessControlAllowHeaders = StringUtils.isBlank(corsProperties.getAllowedHeaders()) ? HttpUtils.accessControlAllowHeaders(request, response) : corsProperties.getAllowedHeaders();

                //  Access-Control-Allow-Headers是一个HTTP响应头，用于指定客户端可以在预检请求中使用哪些HTTP请求头信息。预检请求是浏览器在发送跨域请求之前发送的一种OPTIONS
                //  请求，用于检测实际请求是否安全。在预检请求中，浏览器会向服务端发送一些额外的请求头信息，例如Authorization、Content-Type等，以检查服务端是否允许这些请求头信息。
                // 如果服务端不允许某些请求头信息，浏览器将会收到一个错误响应。为了避免这种情况，您可以在服务端的HTTP响应头中添加Access-Control-Allow-Headers
                // 头信息，以允许客户端使用指定的HTTP请求头信息。例如
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, accessControlAllowHeaders);
                //  Access-Control-Expose-Headers是一个HTTP响应头，用于指定哪些HTTP响应头信息可以被客户端访问。
                //  默认情况下，客户端只能访问以下响应头信息 ：
                //  Cache-Control 、Content-Language 、Content-Type、Expires、Last-Modified、Pragma
                //  如果您的服务端在响应头中添加了自定义的HTTP响应头信息，例如Authorization，客户端将无法访问该响应头信息
                //  此时，您可以在服务端的HTTP响应头中添加Access-Control-Expose-Headers头信息，以允许客户端访问指定的HTTP响应头信息。
                response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, accessControlAllowHeaders);


                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, corsProperties.getAllowCredentials() + "");

                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, corsProperties.getAllowedMethods());


            } catch (Throwable e) {
                if (log.isInfoEnabled()) {
                    log.info("[unkown] 跨域支持捕获到未知异常 {}", e.getMessage());
                }

            }

            filterChain.doFilter(request, response);
        }

        public CustomCorsFilter(WebEnhanceProperties.CorsProperties corsProperties) {
            this.corsProperties = corsProperties;
        }

    }


}
