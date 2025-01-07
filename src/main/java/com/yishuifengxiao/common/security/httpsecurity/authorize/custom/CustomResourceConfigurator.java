/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.custom;

import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.function.Supplier;


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
 * <p>
 * <p>
 * access 方法的回调中有两个参数，第一个参数是 authentication，很明显，这就是当前登录成功的用户对象，从这里就可以提取出来当前用户所具备的权限。
 * <p>
 * 第二个参数 object 实际上是一个 RequestAuthorizationContext，从这个里边可以提取出来当前请求对象 HttpServletRequest，进而提取出来当前请求的 URL 地址，然后依据权限表中的信息，判断出当前请求需要什么权限，再和 authentication 中提取出来的当前用户所具备的权限进行对比即可。
 * <p>
 * 如果当前登录用户具备请求所需要的权限，则返回 new AuthorizationDecision(true);，否则返回 new AuthorizationDecision(false); 即可。
 * </p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CustomResourceConfigurator {

    /**
     * <p>Determines if access is granted for a specific authentication and object.</p>
     *
     * <p>第一个参数是 authentication，很明显，这就是当前登录成功的用户对象，从这里就可以提取出来当前用户所具备的权限</p>
     * <p>第二个参数 object 实际上是一个 RequestAuthorizationContext，从这个里边可以提取出来当前请求对象 HttpServletRequest，进而提取出来当前请求的 URL 地址，然后依据权限表中的信息，判断出当前请求需要什么权限，再和 authentication 中提取出来的当前用户所具备的权限进行对比即可</p>
     * <p>如果当前登录用户具备请求所需要的权限，则返回 new AuthorizationDecision(true);，否则返回 new AuthorizationDecision(false); 即可</p>
     *
     * @param authentication the {@link Supplier} of the {@link Authentication} to check
     * @param object         the {@link T} object to check
     * @return an {@link AuthorizationDecision} or null if no decision could be made
     */
    @Nullable
    AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object);

    /**
     * SimpleSecurityGlobalEnhanceFilter strategy to match an HttpServletRequest.
     *
     * @return RequestMatcher
     */
    RequestMatcher requestMatcher();
}
