package com.yishuifengxiao.common.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * web增强支持支持属性配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.web")
public class WebFilterProperties {

    /**
     * 是否开启增强支持,默认开启
     */
    private Boolean enable = true;
    /**
     * 请求追踪标识符的名字
     */
    private String trackingIdentifier = "request-traced-ssid";

    /**
     * 是否开启动态修改日志级别功能，若不为空则表示开启此功能
     */
    private String dynamicLogLevel;
    /**
     * 动态日志内容获取参数
     */
    private String dynamicLogParameter = "dynamicLogLevel";

}
