package com.yishuifengxiao.common.security.httpsecurity.authorize.customizer;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceConfigurator;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.support.ResourceHelper;
import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 资源设置处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ResourceHttpSecurityEnhanceCustomizer implements HttpSecurityEnhanceCustomizer {


    /**
     * key CustomResourceProvider的名字
     * value CustomResourceProvider的实例
     */
    private Map<String, CustomResourceConfigurator> customResourceConfigurators;
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void apply(SecurityPropertyResource securityPropertyResource,
                      AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
//        permitAll - 该请求不需要授权即可调用；注意，在这种情况下，将不会从 session 中检索 Authentication。
//        denyAll - 该请求在任何情况下都是不允许的；注意在这种情况下，永远不会从会话中检索 Authentication。
//        hasAuthority - 请求要求 Authentication 的 GrantedAuthority 符合给定值。
//        hasRole - hasAuthority 的快捷方式，前缀为 ROLE_ 或任何配置为默认前缀的内容。
//        hasAnyAuthority - 请求要 Authentication 具有符合任何给定值的 GrantedAuthority。
//        hasAnyRole - hasAnyAuthority 的一个快捷方式，其前缀为 ROLE_ 或任何被配置为默认的前缀。
//        hasPermission - 用于对象级授权的 PermissionEvaluator 实例的 hook。

        //赞成 permitAll 而不是 ignoring
        // 因为即使是静态资源，写入安全 header 也很重要，如果请求被忽略，Spring Security 就无法写入安全 header。
        //在过去，由于 Spring Security 会在每个请求中查询 session，因此会影响性能。然而，从 Spring Security 6 开始，除非授权规则要求，
        // 否则会话不再被 ping。由于现在已经解决了对性能的影响，Spring Security 建议对所有请求至少使用 permitAll


        List<ResourceHelper.UrlResource> resources =
                ResourceHelper.extractAllResources(this.requestMappingHandlerMapping).stream().map(v -> {
                    PreAuthorize preAuthorize = AnnotationUtils.findAnnotation(v.getMethod(),
                            PreAuthorize.class);
                    if (null != preAuthorize && StringUtils.isNotBlank(preAuthorize.value())) {
                        v.setPreAuthorize(preAuthorize.value());
                        return v;
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        http.authorizeHttpRequests((authorizeHttpRequests) -> {
            if (securityPropertyResource.security().getResource().getPermitAll()) {
                authorizeHttpRequests.anyRequest().permitAll();
            } else {
                authorizeHttpRequests.requestMatchers(HttpMethod.OPTIONS).permitAll();
                authorizeHttpRequests.dispatcherTypeMatchers(DispatcherType.FORWARD,
                        DispatcherType.ERROR).permitAll();
                // 所有直接放行的资源
                authorizeHttpRequests.requestMatchers(securityPropertyResource.permitAll()).permitAll();
                // 所有匿名访问的资源
                authorizeHttpRequests.requestMatchers(securityPropertyResource.anonymous()).anonymous();


                // 所有自定义权限路径的资源
                if (null != this.customResourceConfigurators) {
                    customResourceConfigurators.forEach((providerName, provider) -> {
                        if (null != provider && null != provider.requestMatcher()) {
                            authorizeHttpRequests.requestMatchers(provider.requestMatcher()).access(new AuthorizationManager<>() {
                                @Override
                                public void verify(Supplier<Authentication> authentication,
                                                   RequestAuthorizationContext object) {
                                    AuthorizationManager.super.verify(authentication, object);
                                }

                                @Override
                                public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
                                    Boolean value = authorizationDecision(resources, object,
                                            authentication);
                                    if (null != value) {
                                        return new AuthorizationDecision(value);
                                    }
                                    return provider.check(authentication, object);
                                }
                            });
                        }

                    });
                } else {
                    if (null != resources && !resources.isEmpty()) {
                        List<RequestMatcher> matchers =
                                resources.stream().map(v -> new AntPathRequestMatcher(v.getUri(),
                                        v.getRequestMethod().name())).collect(Collectors.toList());
                        authorizeHttpRequests.requestMatchers(new OrRequestMatcher(matchers)).access(new AuthorizationManager<>() {
                            @Override
                            public void verify(Supplier<Authentication> authentication,
                                               RequestAuthorizationContext object) {
                                AuthorizationManager.super.verify(authentication, object);
                            }

                            @Override
                            public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
                                Boolean value = authorizationDecision(resources,
                                        object, authentication);
                                if (value != null) {
                                    return new AuthorizationDecision(value);
                                }
                                return new AuthorizationDecision(false);
                            }
                        });
                    }

                }

                //只要经过了授权就能访问
//                authorizeHttpRequests.requestMatchers(new NegatedRequestMatcher(new
//                OrRequestMatcher
//                (requestMatchers.stream().filter(Objects::nonNull).distinct().collect
//                (Collectors.toList()))))
//                .authenticated();
                authorizeHttpRequests.anyRequest().authenticated();
            }
        });


    }

    private static Boolean authorizationDecision(List<ResourceHelper.UrlResource> resources,
                                                 RequestAuthorizationContext context,
                                                 Supplier<Authentication> authentication) {
        if (null == resources || resources.isEmpty()) {
            return null;
        }
        ResourceHelper.UrlResource resource =
                resources.parallelStream().filter(v -> new AntPathRequestMatcher(v.getUri(),
                        v.getRequestMethod().name()).matches(context.getRequest())).findFirst().orElse(null);
        if (null != resource) {
            DefaultHttpSecurityExpressionHandler defaultHttpSecurityExpressionHandler =
                    new DefaultHttpSecurityExpressionHandler();
            EvaluationContext evaluationContext =
                    defaultHttpSecurityExpressionHandler.createEvaluationContext(authentication,
                            context);
            // 使用 SpEL 解析表达式
            ExpressionParser parser = new SpelExpressionParser();
            Boolean value =
                    parser.parseExpression(resource.getPreAuthorize()).getValue(evaluationContext
                            , Boolean.class);
            if (null != value) {
                return value;
            }
        }
        return null;
    }


    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }


    public Map<String, CustomResourceConfigurator> getCustomResourceConfigurators() {
        return customResourceConfigurators;
    }

    public void setCustomResourceConfigurators(Map<String, CustomResourceConfigurator> customResourceConfigurators) {
        this.customResourceConfigurators = customResourceConfigurators;
    }

    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return requestMappingHandlerMapping;
    }

    public void setRequestMappingHandlerMapping(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }
}
