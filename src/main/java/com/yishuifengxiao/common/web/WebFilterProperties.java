package com.yishuifengxiao.common.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String ssidName = "request-ssid";

}
