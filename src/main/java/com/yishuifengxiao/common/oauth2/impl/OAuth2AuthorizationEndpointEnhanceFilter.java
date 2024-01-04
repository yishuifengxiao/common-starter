package com.yishuifengxiao.common.oauth2.impl;

import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * OAuth2 增强处理
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class OAuth2AuthorizationEndpointEnhanceFilter extends OncePerRequestFilter {



    private final RequestMatcher authorizationEndpointMatcher;

    @SuppressWarnings("unused")
	private final OAuth2AuthorizationServerConfigurer authorizationServerConfigurer;

    private AuthenticationPoint authenticationPoint;

    /**
     * Constructs an {@code OAuth2AuthorizationEndpointFilter} using the provided parameters.
     *
     * @param authorizationServerConfigurer OAuth2AuthorizationServerConfigurer
     */
    public OAuth2AuthorizationEndpointEnhanceFilter(OAuth2AuthorizationServerConfigurer authorizationServerConfigurer
            , AuthenticationPoint authenticationPoint) {
        this.authorizationServerConfigurer = authorizationServerConfigurer;
        this.authorizationEndpointMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        this.authenticationPoint = authenticationPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!this.authorizationEndpointMatcher.matches(request) || !StringUtils.equalsIgnoreCase(request.getMethod(),
                HttpMethod.GET.name())) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean anonymous = true;
        // oauth2相关的端点
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            anonymous = true;
        } else {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean anonymousUser = StringUtils.equalsIgnoreCase(Objects.toString(authentication.getPrincipal()),
                    "anonymousUser");
            boolean roleAnonymous = null == authorities ? false :
                    authorities.stream().filter(Objects::nonNull).anyMatch(v -> StringUtils.equalsIgnoreCase(v.getAuthority(), "ROLE_ANONYMOUS"));
            anonymous = anonymousUser && roleAnonymous;
        }
        if (anonymous) {
            this.authenticationPoint.handle(request, response, new AccessDeniedException("oauth2 enhance"));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
