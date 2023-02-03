package com.yishuifengxiao.common.security.httpsecurity.authorize;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 抽象授权提供其
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractAuthorizeProvider implements AuthorizeProvider {
    @Override
    public void apply(PropertyResource propertyResource, SecurityHandler securityHandler, HttpSecurity http) throws Exception {
        this.configure(http, securityHandler);
    }

    /**
     * 授权配置
     *
     * @param http
     * @param securityHandler
     * @throws Exception
     */
    protected abstract void configure(HttpSecurity http, SecurityHandler securityHandler) throws Exception;
}
