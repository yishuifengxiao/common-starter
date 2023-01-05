package com.yishuifengxiao.common.web;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import com.yishuifengxiao.common.web.filter.BaseFilter;
import com.yishuifengxiao.common.web.filter.ComposeEnhancedSupportFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.DispatcherServlet;

import com.yishuifengxiao.common.tool.random.UID;

import lombok.extern.slf4j.Slf4j;

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
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties({WebFilterProperties.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.web", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class WebFilterAutoConfiguration {

    /**
     * 请求跟踪拦截器用于增加一个请求追踪标志
     *
     * @return
     */
    @Bean("requestTrackingFilter")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(name = "requestTrackingFilter")
    public Filter requestTrackingFilter(WebFilterProperties webProperties) {
        return (request, response, chain) -> {
            String ssid = UID.uuid();
            request.setAttribute(webProperties.getSsidName(), ssid);
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


    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【易水组件】: 开启 <web增强支持> 相关的配置");
    }
}
