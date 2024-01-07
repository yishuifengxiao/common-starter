package com.yishuifengxiao.common.oauth2resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 资源服务器相关属性配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.security.oauth2resource")
public class ResourceProperties {
    /**
     * 指向认证服务器里token校验地址,一般默认的uri为/oauth2server/check_token
     */
    private String tokenCheckUrl;

}
