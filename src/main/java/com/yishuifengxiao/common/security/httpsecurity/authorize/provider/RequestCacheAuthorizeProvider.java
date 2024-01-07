package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * <p>保存认证之间的请求</p>
 * <p>正如在 处理 Security 异常 中所说明的，当一个请求没有认证，并且是针对需要认证的资源时，有必要保存认证资源的请求，以便在认证成功后重新请求。在Spring Security中，这是通过使用 RequestCache 实现来保存 HttpServletRequest 的。
 * </p>
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/7 12:07
 * @since 1.0.0
 */
public class RequestCacheAuthorizeProvider implements AuthorizeProvider {
    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(securityPropertyResource.security().getSession().getMatchingRequestParameterName());
        http.requestCache(httpSecurityRequestCacheConfigurer -> {

            httpSecurityRequestCacheConfigurer.requestCache(requestCache);
        });
    }

    @Override
    public int order() {
        return 200;
    }
}
