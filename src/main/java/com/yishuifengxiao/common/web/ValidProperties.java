package com.yishuifengxiao.common.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aop模块配置项参数
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.aop")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidProperties {

	/**
	 * 是否开启全局参数校验拦截,默认为true
	 */
	private Boolean enable = true;

}
