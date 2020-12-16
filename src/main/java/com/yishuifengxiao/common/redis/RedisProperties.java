package com.yishuifengxiao.common.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Redis配置
 * @author qingteng
 * @date 2020年11月2日
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.redis")
public class RedisProperties {
	
	/**
	 * 是否开启Redis配置功能，默认为开启
	 */
	private Boolean enable = true;

}
