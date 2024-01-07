package com.yishuifengxiao.common.security.websecurity;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * web安全授权器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface WebSecurityEnhanceCustomizer {
    /**
     * 配置WebSecurity 管理
     *
     * @param securityPropertyResource 资源管理器
     * @param web              WebSecurity
     */
    void configure(SecurityPropertyResource securityPropertyResource, WebSecurity web) ;
}
