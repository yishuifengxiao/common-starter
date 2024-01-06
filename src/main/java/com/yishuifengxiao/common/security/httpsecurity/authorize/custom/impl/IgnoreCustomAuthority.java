/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.custom.impl;

import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;


import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 默认的授权表达式实现
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class IgnoreCustomAuthority implements CustomResourceProvider {

    /**
     * 请求路径前缀(防止设置了项目名)
     */
    private String prefix = "";
    /**
     * 路径匹配器
     */
    private AntPathMatcher matcher = new AntPathMatcher();
    /**
     * 需要忽视的路径
     */
    private Set<String> urls = new HashSet<>();


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        boolean isMatch = false;
        matcher.setCaseSensitive(false);
        log.debug("【授权管理】所有需要忽视的目录为 {}", urls);
        // 获取到请求的uri
        String path = object.getRequest().getRequestURI();
        Authentication auth = authentication.get();
        if (auth == null || auth.getName() == null
                || StringUtils.equalsIgnoreCase(auth.getName(), "anonymousUser")) {
            for (String url : urls) {
                boolean orinal = matcher.match(url.trim(), path);
                log.debug("> 1) 当前请求路径为{},匹配路径为{},匹配结果为{}", path, url, orinal);
                boolean preOrinal = matcher.match(prefix.trim() + url.trim(), path);
                log.debug("> 2) 当前请求路径为{},匹配路径为{},匹配结果为{}", path, prefix.trim() + url.trim(), preOrinal);
                if (orinal || preOrinal) {
                    isMatch = true;
                    break;
                }
            }
        } else {
            isMatch = true;
        }
        log.debug("【授权管理】当前请求的URI为 {},匹配结果为{}", path, isMatch);
        return new AuthorizationDecision(isMatch);
    }


    @Override
    public RequestMatcher requestMatcher() {
        return null;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public IgnoreCustomAuthority() {

    }

    public IgnoreCustomAuthority(String prefix, Set<String> urls) {
        this.prefix = prefix;
        this.urls = urls;
    }

}
