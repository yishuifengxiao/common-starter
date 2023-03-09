package com.yishuifengxiao.common.security.httpsecurity.filter;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.httpsecurity.SecurityRequestFilter;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.token.SecurityValueExtractor;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 登陆时用户名和密码校验
 * </p>
 * <p>
 * 即在系统默认校验之前检查一下用户名和密码是否正确 ,
 * <p>
 * 用于在UsernamePasswordAuthenticationFilter
 * 之前提前校验一下用户名是否已经存在,会在UsernameAuthInterceptor中被收集注入
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class UsernamePasswordPreAuthFilter extends SecurityRequestFilter {

    private AntPathRequestMatcher pathMatcher = null;

    private SecurityHandler securityHandler;

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private PropertyResource propertyResource;

    /**
     * 信息提取器
     */
    private SecurityValueExtractor securityValueExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 是否关闭前置参数校验功能
        Boolean closePreAuth = propertyResource.security().getClosePreAuth();
        if (BooleanUtils.isNotTrue(closePreAuth)) {
            AntPathRequestMatcher pathMatcher = this.antPathMatcher();
            if (pathMatcher.matches(request)) {

                String username = securityValueExtractor.extractUsername(request, response);
                String password = securityValueExtractor.extractPassword(request, response);

                if (username == null) {
                    username = "";
                }
                if (password == null) {
                    password = "";
                }

                username = username.trim();

                try {
                    // 获取认证信息
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (null == userDetails) {
                            throw new CustomException(ErrorCode.USERNAME_NO_EXTIS,
                                    propertyResource.security().getMsg().getAccountNoExtis());
                        }
                        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                            throw new CustomException(ErrorCode.PASSWORD_ERROR,
                                    propertyResource.security().getMsg().getPasswordIsError());
                        }
                    } catch (UsernameNotFoundException ex) {
                        throw new CustomException(ErrorCode.USERNAME_NO_EXTIS,
                                propertyResource.security().getMsg().getAccountNoExtis());
                    }
                } catch (Exception exception) {
                    securityHandler.whenAuthenticationFailure(propertyResource, request, response, exception);
                    return;
                }

            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(this, UsernamePasswordAuthenticationFilter.class);

    }

    /**
     * 获取到路径匹配器
     *
     * @return 路径匹配器
     */
    private AntPathRequestMatcher antPathMatcher() {
        if (null == this.pathMatcher) {
            this.pathMatcher = new AntPathRequestMatcher(this.propertyResource.security().getFormActionUrl());
        }
        return this.pathMatcher;
    }

    // @formatter:off
    public UsernamePasswordPreAuthFilter(SecurityHandler securityHandler,
                                         UserDetailsService userDetailsService,
                                         PasswordEncoder passwordEncoder,
                                         PropertyResource propertyResource,
                                         SecurityValueExtractor securityValueExtractor) {
        this.securityHandler = securityHandler;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.propertyResource = propertyResource;
        this.securityValueExtractor = securityValueExtractor;
    }
    // @formatter:on
}
