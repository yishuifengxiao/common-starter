package com.yishuifengxiao.common.security.httpsecurity.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.yishuifengxiao.common.security.token.SecurityContextExtractor;
import com.yishuifengxiao.common.security.httpsecurity.authorize.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.httpsecurity.SecurityRequestFilter;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

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

    private HandlerProcessor handlerProcessor;

    private SecurityHelper securityHelper;

    private PropertyResource propertyResource;

    /**
     * 信息提取器
     */
    private SecurityContextExtractor securityContextExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 是否关闭前置参数校验功能
        Boolean closePreAuth = propertyResource.security().getClosePreAuth();
        if (BooleanUtils.isNotTrue(closePreAuth)) {
            AntPathRequestMatcher pathMatcher = this.antPathMatcher();
            if (pathMatcher.matches(request)) {

                String username = securityContextExtractor.extractUsername(request, response);
                String password = securityContextExtractor.extractPassword(request, response);

                if (username == null) {
                    username = "";
                }
                if (password == null) {
                    password = "";
                }

                username = username.trim();

                try {
                    // 生成token
                    String sessionId = securityContextExtractor.extractUserUniqueIdentifier(request, response);
                    // 生成token,这一步操作中已经产生了Authentication并放入上下文中
                    SecurityToken token = securityHelper.create(username, password, sessionId);
                    // 获取认证信息
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    handlerProcessor.login(propertyResource, request, response, authentication, token);
                    return;
                } catch (CustomException exception) {
                    handlerProcessor.failure(propertyResource, request, response, exception);
                    return;
                } catch (Exception e) {
                    handlerProcessor.exception(propertyResource, request, response, e);
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

    public UsernamePasswordPreAuthFilter(HandlerProcessor handlerProcessor, SecurityHelper securityHelper,
                                         PropertyResource propertyResource, SecurityContextExtractor securityContextExtractor) {
        this.handlerProcessor = handlerProcessor;
        this.securityHelper = securityHelper;
        this.propertyResource = propertyResource;
        this.securityContextExtractor = securityContextExtractor;
    }

}
