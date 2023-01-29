package com.yishuifengxiao.common.security.httpsecurity;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.httpsecurity.filter.SecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.interceptor.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统安全管理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleHttpSecurityManager implements HttpSecurityManager, InitializingBean {
    /**
     * 系统中所有的Security 请求过滤器 实例
     */
    private List<SecurityRequestFilter> securityRequestFilters;
    /**
     * 收集到所有的授权配置，Order的值越小，实例排在队列的越前面，这里需要使用有序队列
     */
    private List<AuthorizeProvider> authorizeProviders;

    /**
     * 资源授权拦截器
     */
    private List<HttpSecurityInterceptor> interceptors;


    /**
     * 资源路径器
     */
    private PropertyResource propertyResource;


    @Override
    public void config(HttpSecurity http) throws Exception {

        if (null != this.securityRequestFilters) {
            for (SecurityRequestFilter securityRequestFilter : this.securityRequestFilters) {
                if (propertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( Security请求过滤器 ) 实例为 {}", securityRequestFilter);
                }
                securityRequestFilter.configure(http);
            }
        }

        // 将HttpSecurityInterceptor 实例装载到security中
        if (null != this.interceptors) {
            for (HttpSecurityInterceptor interceptor : this.interceptors) {
                if (propertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( 资源授权拦截器 ) 实例为 {}", interceptor);
                }
                http.apply(interceptor);
            }
        }
        // 加入自定义的授权配置
        if (null != this.authorizeProviders) {
            for (AuthorizeProvider authorizeConfigProvider : authorizeProviders) {
                if (propertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( 授权提供器 ) 序号为 {} , 实例为 {}", authorizeConfigProvider.getOrder(), authorizeConfigProvider);
                }

                authorizeConfigProvider.config(propertyResource, http.authorizeRequests());

            }

        }

    }


    public SimpleHttpSecurityManager(List<AuthorizeProvider> authorizeProviders, List<HttpSecurityInterceptor> interceptors, PropertyResource propertyResource, List<SecurityRequestFilter> securityRequestFilters) {

        this.authorizeProviders = authorizeProviders;
        this.interceptors = interceptors;
        this.propertyResource = propertyResource;
        this.securityRequestFilters = securityRequestFilters;
    }

    @Override
    public void afterPropertiesSet() {
        this.authorizeProviders = this.authorizeProviders.stream().filter(Objects::nonNull).sorted(Comparator.comparing(AuthorizeProvider::getOrder)).collect(Collectors.toList());
    }
}
