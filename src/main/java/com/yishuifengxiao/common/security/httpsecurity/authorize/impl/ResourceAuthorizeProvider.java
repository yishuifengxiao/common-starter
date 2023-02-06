package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * 资源设置处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceAuthorizeProvider implements AuthorizeProvider {


    /**
     * 实例的名字必须为 <code>customResourceProvider</code>
     */
    private CustomResourceProvider customResourceProvider;

    @Override
    public void apply(PropertyResource propertyResource, SecurityHandler securityHandler, HttpSecurity http) throws Exception {


        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();

        registry.mvcMatchers(HttpMethod.OPTIONS).permitAll();
        registry.antMatchers(HttpMethod.OPTIONS).permitAll();
//        registry.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
        // 所有忽视的资源
        for (String url : propertyResource.allIgnoreUrls()) {
            registry.antMatchers(url).permitAll();
        }
        // 所有直接放行的资源
        for (String url : propertyResource.allPermitUrs()) {
            registry.antMatchers(url).permitAll();
        }

        for (String url : propertyResource.anonymousUrls()) {
            registry.antMatchers(url).anonymous();
        }
        // 所有自定义权限路径的资源
        if (null != this.customResourceProvider) {
            for (String path : propertyResource.allCustomUrls()) {
                // 自定义权限
                registry.antMatchers(path).access("@customResourceProvider.hasPermission(request, authentication)");
                registry.mvcMatchers(path).access("@customResourceProvider.hasPermission(request, authentication)");
            }
        }


        //只要经过了授权就能访问
        registry.anyRequest().authenticated();

    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    public CustomResourceProvider getCustomResourceProvider() {
        return customResourceProvider;
    }

    public void setCustomResourceProvider(CustomResourceProvider customResourceProvider) {
        this.customResourceProvider = customResourceProvider;
    }
}
