/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.custom;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 自定义授权提供器
 * </p>
 * <p> 实例的名字必须为 <code>customResourceProvider</code></p>
 * <p>
 * 用户根据自己实际项目需要确定如何根据实际项目变化配置是否给予授权,
 * <p>
 * 在使用时，自定义授权提供器实例会被注入到<code>CustomAuthorizeProvider</code>中
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CustomResourceProvider {
    /**
     * 自定义权限判断
     *
     * @param request HttpServletRequest
     * @param authentication    Authentication
     * @return true表示允许授权
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication);

    /**
     * SimpleSecurityGlobalEnhance strategy to match an HttpServletRequest.
     *
     * @return RequestMatcher
     */
    RequestMatcher requestMatcher();
}
