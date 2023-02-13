package com.yishuifengxiao.common.security.httpsecurity.filter;

import com.yishuifengxiao.common.security.httpsecurity.SecurityRequestFilter;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.SecurityTokenExtractor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * 用于在非oauth2的情况下，在仅仅使用spring Security时系统从用户提供的请求里解析出认证信息，判断用户是否能够认证
 * </p>
 * 在此情况下，除了忽视资源和非管理资源不需要经过该逻辑，理论上一版情况下其他资源都要经过该逻辑
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class TokenValidateFilter extends SecurityRequestFilter implements InitializingBean {

    private Map<String, AntPathRequestMatcher> map = new HashMap<>();


    private PropertyResource propertyResource;

    private SecurityHandler securityHandler;

    private SecurityTokenExtractor securityTokenExtractor;

    private SecurityHelper securityHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (BooleanUtils.isTrue(propertyResource.security().isOpenTokenFilter())) {
                // 先判断请求是否需要经过授权校验
                boolean noRequiresAuthentication = propertyResource.allUnCheckUrls().parallelStream().anyMatch(url -> getMatcher(url).matches(request));
                if (propertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】请求 {} 是否需要进行校验校验的结果为 {}", request.getRequestURI(), !noRequiresAuthentication);
                }
                if (!noRequiresAuthentication) {
                    // 从请求中获取到携带的认证
                    String tokenValue = securityTokenExtractor.extractTokenValue(request, response, propertyResource);

                    if (propertyResource.showDetail()) {
                        log.info("【yishuifengxiao-common-spring-boot-starter】请求 {} 携带的认证信息为 {}", request.getRequestURI(), tokenValue);
                    }

                    if (StringUtils.isNotBlank(tokenValue)) {
                        // 该请求携带了认证信息
                        Authentication authentication = securityHelper.authorize(tokenValue);
                        // 将认证信息注入到spring Security中
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (CustomException e) {
            securityHandler.preAuth(propertyResource, request, response, Response.of(propertyResource.security().getMsg().getInvalidTokenValueCode(), e.getMessage(), e));
            return;
        } catch (Exception e) {
            securityHandler.onException(propertyResource, request, response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }


    /**
     * 根据url获取匹配器
     *
     * @param url
     * @return
     */
    private synchronized AntPathRequestMatcher getMatcher(String url) {
        AntPathRequestMatcher matcher = map.get(url);
        if (null == matcher) {
            matcher = new AntPathRequestMatcher(url);
            map.put(url, matcher);
        }
        return matcher;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(this, LogoutFilter.class);

    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

    }

    public TokenValidateFilter(PropertyResource propertyResource, SecurityHandler securityHandler,
                               SecurityTokenExtractor securityTokenExtractor, SecurityHelper securityHelper) {
        this.propertyResource = propertyResource;
        this.securityHandler = securityHandler;
        this.securityTokenExtractor = securityTokenExtractor;
        this.securityHelper = securityHelper;

    }

}
