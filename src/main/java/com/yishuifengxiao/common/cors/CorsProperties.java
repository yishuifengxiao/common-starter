/**
 * 
 */
package com.yishuifengxiao.common.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 跨域参数配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.cors")
public class CorsProperties {
	/**
	 * 是否开启跨域支持,默认开启
	 */
	private Boolean enable = true;
	/**
	 * 跨域设置允许的路径，默认为所有路径
	 */
	private String url = "/**";
	/**
	 * 跨域设置允许的Origins，默认为所有
	 */
	private String allowedOrigins = "*";
	/**
	 * 跨域设置允许的请求方法，默认为所有
	 */
	private String allowedMethods = "*";
	/**
	 * 跨域设置允许的请求头，默认为所有
	 */
	private String allowedHeaders = "*";
	/**
	 * 跨域设置是否允许携带凭据，默认为true
	 */
	private Boolean allowCredentials = true;


}
