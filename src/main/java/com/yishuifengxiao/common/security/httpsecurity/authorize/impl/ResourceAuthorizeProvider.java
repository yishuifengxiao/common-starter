package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.support.PropertyResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 资源设置处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceAuthorizeProvider implements AuthorizeProvider {


    /**
     * key CustomResourceProvider的名字
     * value CustomResourceProvider的实例
     */
    private Map<String, CustomResourceProvider> customResourceProviders;

    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {


        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        registry.requestMatchers(HttpMethod.OPTIONS).permitAll();
//        registry.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
        // 所有忽视的资源
        for (String url : propertyResource.allIgnoreUrls()) {
            registry.requestMatchers(url).permitAll();
        }
        // 所有直接放行的资源
        for (String url : propertyResource.allPermitUrs()) {
            registry.requestMatchers(url).permitAll();
        }

        for (String url : propertyResource.anonymousUrls()) {
            registry.requestMatchers(url).anonymous();
        }
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        // 所有已经明确了权限的路径
        propertyResource.definedUrls().stream().map(AntPathRequestMatcher::new).forEach(requestMatchers::add);
        // 所有自定义权限路径的资源
        if (null != this.customResourceProviders) {
            customResourceProviders.forEach((providerName, provider) -> {
                if (null != provider && null != provider.requestMatcher()) {
                    registry.requestMatchers(provider.requestMatcher()).access("@" + providerName + ".hasPermission" + "(request, authentication)");
                    // 增加配置
                    requestMatchers.add(provider.requestMatcher());
                }

            });
        }
        //只要经过了授权就能访问
        registry.requestMatchers(new NegatedRequestMatcher(new OrRequestMatcher(requestMatchers.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList())))).authenticated();

    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    public Map<String, CustomResourceProvider> getCustomResourceProviders() {
        return customResourceProviders;
    }

    public void setCustomResourceProviders(Map<String, CustomResourceProvider> customResourceProviders) {
        this.customResourceProviders = customResourceProviders;
    }
}
