package com.yishuifengxiao.common.security.websecurity;

import com.yishuifengxiao.common.security.support.PropertyResource;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * web安全授权器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface WebSecurityProvider {
    /**
     * 配置WebSecurity 管理
     *
     * @param propertyResource 资源管理器
     * @param web              WebSecurity
     */
    void configure(PropertyResource propertyResource, WebSecurity web) ;
}
