/**
 * 
 */
package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 跨域参数配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
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

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	/**
	 * 跨域设置允许的路径，默认为所有路径
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 跨域设置允许的Origins，默认为所有
	 */
	public String getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	/**
	 * 跨域设置允许的请求方法，默认为所有
	 */
	public String getAllowedMethods() {
		return allowedMethods;
	}

	public void setAllowedMethods(String allowedMethods) {
		this.allowedMethods = allowedMethods;
	}

	/**
	 * 跨域设置允许的请求头，默认为所有
	 */
	public String getAllowedHeaders() {
		return allowedHeaders;
	}

	public void setAllowedHeaders(String allowedHeaders) {
		this.allowedHeaders = allowedHeaders;
	}

	/**
	 * 跨域设置是否允许携带凭据，默认为true
	 */
	public Boolean getAllowCredentials() {
		return allowCredentials;
	}

	public void setAllowCredentials(Boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}

}
