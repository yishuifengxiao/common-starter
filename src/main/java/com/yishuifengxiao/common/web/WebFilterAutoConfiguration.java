package com.yishuifengxiao.common.web;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.random.UID;
import com.yishuifengxiao.common.web.filter.BaseFilter;
import com.yishuifengxiao.common.web.filter.ComposeEnhancedSupportFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * web增强支持
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@ControllerAdvice
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties({WebFilterProperties.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class WebFilterAutoConfiguration implements ResponseBodyAdvice {

    @Autowired
    private WebFilterProperties webProperties;

    /**
     * 请求跟踪拦截器用于增加一个请求追踪标志
     *
     * @return
     */
    @Bean("requestTrackingFilter")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(name = "requestTrackingFilter")
    public Filter requestTrackingFilter() {
        return (request, response, chain) -> {
            String ssid = UID.uuid();
            request.setAttribute(webProperties.getSsidName(), ssid);
            TraceContext.set(ssid);
            chain.doFilter(request, response);
        };

    }


    /**
     * 注入一个过滤器增强支持处理器
     *
     * @return 过滤器增强支持处理器
     */
    @Bean("composeEnhancedSupportFilter")
    @ConditionalOnMissingBean(name = "composeEnhancedSupportFilter")
    public FilterRegistrationBean<ComposeEnhancedSupportFilter> composeFilter(List<BaseFilter> filters) {
        ComposeEnhancedSupportFilter composeEnhancedSupportFilter = new ComposeEnhancedSupportFilter(filters);
        FilterRegistrationBean<ComposeEnhancedSupportFilter> registration = new FilterRegistrationBean<>(composeEnhancedSupportFilter);
        registration.setName("composeEnhancedSupportFilter");
        registration.setUrlPatterns(Arrays.asList("/*"));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.hasMethodAnnotation(ResponseBody.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            if (null != body && body instanceof Response) {
                Response result = (Response) body;
                HttpServletRequest httpServerHttpRequest = ((ServletServerHttpRequest) request).getServletRequest();
                Object attribute = httpServerHttpRequest.getAttribute(webProperties.getSsidName());
                if (null == attribute || StringUtils.isBlank(attribute.toString())) {
                    attribute = TraceContext.get();

                }
                if (null != attribute) {
                    result.setId(attribute.toString());
                }
                return result;
            }
        } catch (Exception e) {
            log.debug("【yishuifengxiao-common-spring-boot-starter】:There was a problem obtaining the request tracking id {}", e);
        }

        return body;
    }

    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <web增强支持> 相关的配置");
    }
}
