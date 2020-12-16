package com.yishuifengxiao.common.aop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aop相关的配置参数
 * 
 * @author yishui
 * @date 2020年6月17日
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.aop")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AopProperties {

	/**
	 * 是否开启全局参数校验拦截,默认为true
	 */
	private Boolean enable = true;
		
}
