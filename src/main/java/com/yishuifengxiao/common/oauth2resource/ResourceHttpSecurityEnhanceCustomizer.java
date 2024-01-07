package com.yishuifengxiao.common.oauth2resource;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * 资源服务器授权提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceHttpSecurityEnhanceCustomizer implements HttpSecurityEnhanceCustomizer {


    private BearerTokenResolver customBearerTokenResolver;

    private OpaqueTokenIntrospector customOpaqueTokenIntrospector;


    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off


		http.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {

                    httpSecurityOAuth2ResourceServerConfigurer
                            .authenticationEntryPoint(authenticationPoint)
                            .accessDeniedHandler(authenticationPoint)
                            .bearerTokenResolver(customBearerTokenResolver)
                            .jwt(jwtConfigurer ->{
                                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter());
                             })
                            .opaqueToken(opaqueTokenConfigurer -> {
                                opaqueTokenConfigurer.introspector(customOpaqueTokenIntrospector);

                            });

                });

		//@formatter:on  
    }

    @Override
    public int order() {
        return 2000;
    }



    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        然而，在很多情况下，这个默认值是不够的。例如，有些授权服务器并不使用 scope 属性，而是有自己的自定义属性。或者，在其他时候，资源服务器可能需要将属性或属性的构成调整为内部化的授权。
//        为此，Spring Security提供了 JwtAuthenticationConverter，它负责 将 Jwt 转换为 Authentication。默认情况下，Spring Security会将 JwtAuthenticationProvider 与 JwtAuthenticationConverter 的默认实例连接起来。
//        作为配置 JwtAuthenticationConverter 的一部分，你可以提供一个附属的转换器，从 Jwt 到授予权限集合（Collection）。

        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        //        假设你的授权服务器在一个名为 authorities 的自定义 claim 中交流授权。在这种情况下，你可以配置 JwtAuthenticationConverter 应该检查的 claim，像这样。
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
//        你也可以把权限的前缀配置成不同的。你可以像这样把每个权限的前缀改为 ROLE_，而不是用 SCOPE_。
//        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//        或者，你可以通过调用 JwtGrantedAuthoritiesConverter#setAuthorityPrefix("") 完全删除前缀。

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    static class CustomAuthenticationConverter implements org.springframework.core.convert.converter.Converter<Jwt,
            AbstractAuthenticationToken> {

        @Override
        public AbstractAuthenticationToken convert(Jwt source) {
            return new JwtAuthenticationToken(source);
        }
    }


    public BearerTokenResolver getCustomBearerTokenResolver() {
        return customBearerTokenResolver;
    }

    public void setCustomBearerTokenResolver(BearerTokenResolver customBearerTokenResolver) {
        this.customBearerTokenResolver = customBearerTokenResolver;
    }

    public OpaqueTokenIntrospector getCustomOpaqueTokenIntrospector() {
        return customOpaqueTokenIntrospector;
    }

    public void setCustomOpaqueTokenIntrospector(OpaqueTokenIntrospector customOpaqueTokenIntrospector) {
        this.customOpaqueTokenIntrospector = customOpaqueTokenIntrospector;
    }


}
