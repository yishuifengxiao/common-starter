package com.yishuifengxiao.common.security.httpsecurity.authorize.custom.impl;

import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.function.Supplier;


/**
 * 自定义授权的默认实现
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleCustomResourceProvider implements CustomResourceProvider {


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();
        Authentication auth = authentication.get();
        log.debug("【自定义授权】自定义授权的路径为 {}，认证信息为 {}  ", request.getRequestURI(), auth);

        return new AuthorizationDecision(true);
    }

    @Override
    public RequestMatcher requestMatcher() {
        return null;
    }

}
