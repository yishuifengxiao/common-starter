package com.yishuifengxiao.common.oauth2server;

import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;

/**
 * oauth2server.1增强扩展配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface OAuth2AuthorizationProvider {

    /**
     * 配置增强扩展
     *
     * @param authorizationServerConfigurer
     */
    void apply(OAuth2AuthorizationServerConfigurer authorizationServerConfigurer);
}
