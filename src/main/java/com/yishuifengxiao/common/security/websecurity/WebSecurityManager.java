package com.yishuifengxiao.common.security.websecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface WebSecurityManager {

    /**
     * 配置WebSecurity
     *
     * @param web WebSecurity
     */
    void apply(WebSecurity web);
}
