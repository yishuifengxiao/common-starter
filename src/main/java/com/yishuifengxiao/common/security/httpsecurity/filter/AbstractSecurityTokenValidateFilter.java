package com.yishuifengxiao.common.security.httpsecurity.filter;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.exception.ExpireTokenException;
import com.yishuifengxiao.common.security.exception.IllegalTokenException;
import com.yishuifengxiao.common.security.exception.InvalidTokenException;
import com.yishuifengxiao.common.security.httpsecurity.AbstractSecurityRequestFilter;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.authentication.SimpleWebAuthenticationDetails;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityTokenResolver;
import com.yishuifengxiao.common.tool.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 校验token的合法性
 * </p>
 *
 * <p>
 * 即判断用户请求里携带的访问令牌是否为合法且在有效状态 ，同时判断一下该用户的账号的状态
 * </p>
 *
 * <p>
 * 用于在非oauth2的情况下，在仅仅使用spring Security时系统从用户提供的请求里解析出认证信息，判断用户是否能够认证，在此情况下，除了忽视资源和非管理资源不需要经过该逻辑，理论上一版情况下其他资源都要经过该逻辑
 * </p>
 *
 * <p>这里采用bearer token 模式 ，具体规范参见
 * <a href="https://datatracker.ietf.org/doc/html/rfc6750">https://datatracker.ietf.org/doc/html/rfc6750</a></p>
 * <p> HTTP 身份验证 参见
 * <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Authorization">https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Authorization</a></p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class AbstractSecurityTokenValidateFilter extends AbstractSecurityRequestFilter implements InitializingBean {

    private Map<String, AntPathRequestMatcher> map = new HashMap<>();


    private PropertyResource propertyResource;

    private SecurityHandler securityHandler;

    private SecurityTokenResolver securityTokenResolver;


    /**
     * token生成器
     */
    private TokenBuilder tokenBuilder;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 从请求中获取到携带的认证
        String tokenValue = securityTokenResolver.extractTokenValue(request, response, propertyResource);


        if (requiresAuthentication(request)) {
            if (propertyResource.showDetail()) {
                log.info("【yishuifengxiao-common-spring-boot-starter】请求 {} 携带的认证信息为 {}", request.getRequestURI(), tokenValue);
            }
            try {
                if (StringUtils.isBlank(tokenValue)) {
                    throw new IllegalTokenException(propertyResource.security().getMsg().getTokenValueIsNull());
                }

                // 该请求携带了认证信息
                Authentication authentication = authorize(request, tokenValue);

                // 将认证信息注入到spring Security中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AccessDeniedException e) {
                securityHandler.whenAccessDenied(propertyResource, request, response, e);
                return;
            } catch (CustomException e) {
                securityHandler.onException(propertyResource, request, response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    private boolean requiresAuthentication(HttpServletRequest request) {
        if (BooleanUtils.isNotTrue(propertyResource.security().isOpenTokenFilter())) {
            return false;
        }

        boolean matches = new NegatedRequestMatcher(new OrRequestMatcher(propertyResource.permitAll(), propertyResource.anonymous())).matches(request);
        log.debug("【yishuifengxiao-common-spring-boot-starter】请求 {} 是否需要进行校验校验的结果为 {}", request.getRequestURI(), matches);
        return matches;
    }


    /**
     * 验证携带的token信息
     *
     * @param request
     * @param tokenValue
     * @return Authentication
     * @throws AccessDeniedException
     * @throws CustomException
     */
    protected Authentication authorize(HttpServletRequest request, String tokenValue) throws AccessDeniedException, CustomException {

        // 解析token
        SecurityToken token = tokenBuilder.loadByTokenValue(tokenValue);

        if (null == token) {
            throw new IllegalTokenException(ErrorCode.INVALID_TOKEN, propertyResource.security().getMsg().getTokenIsNull());
        }

        if (token.isExpired()) {
            if (propertyResource.showDetail()) {
                log.debug("【yishuifengxiao-common-spring-boot-starter】访问令牌 {} 已过期 ", token);
            }
            // 删除失效的token
            tokenBuilder.remove(token);

            throw new ExpireTokenException(ErrorCode.EXPIRED_ROKEN, propertyResource.security().getMsg().getTokenIsExpired());
        }

        if (!token.isActive()) {
            if (propertyResource.showDetail()) {
                log.debug("【yishuifengxiao-common-spring-boot-starter】访问令牌 {} 已失效 ", token);
            }
            // 删除失效的token
            tokenBuilder.remove(token);

            throw new InvalidTokenException(ErrorCode.EXPIRED_ROKEN, propertyResource.security().getMsg().getTokenIsInvalid());
        }

        // 刷新令牌的过期时间
        token = tokenBuilder.refreshExpireTime(token);

        if (null != SecurityContextHolder.getContext().getAuthentication()) {
            return SecurityContextHolder.getContext().getAuthentication();
        }


        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(token.getPrincipal(), null, token.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new SimpleWebAuthenticationDetails(request, token));
        return usernamePasswordAuthenticationToken;
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(this, BasicAuthenticationFilter.class);

    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

    }

    public AbstractSecurityTokenValidateFilter(PropertyResource propertyResource, SecurityHandler securityHandler, SecurityTokenResolver securityTokenResolver, TokenBuilder tokenBuilder) {
        this.propertyResource = propertyResource;
        this.securityHandler = securityHandler;
        this.securityTokenResolver = securityTokenResolver;
        this.tokenBuilder = tokenBuilder;
    }

}
