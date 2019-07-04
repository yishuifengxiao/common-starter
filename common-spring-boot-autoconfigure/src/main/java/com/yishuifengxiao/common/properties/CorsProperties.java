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

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
