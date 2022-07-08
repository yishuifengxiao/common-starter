/**
 * 
 */
package com.yishuifengxiao.common.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.tool.collections.DataUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 跨域支持属性配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
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
	 * 跨域设置允许的路径，默认为所有路径(/*)
	 */
	private String url = "/*";
	/**
	 * 跨域设置允许的Origins，默认为所有
	 */
	private String allowedOrigins = "*";
	/**
	 * 跨域设置允许的请求方法，默认为所有，也可以为 GET,POST,OPTIONS,PUT,DELETE这种形式
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

	/**
	 * 需要添加的响应头
	 */
	private Map<String, String> headers = new HashMap<>();

	/**
	 * 获取要设置要注册筛选器的URL模式
	 * 
	 * @return 要设置要注册筛选器的URL模式
	 */
	public List<String> getUrlPatterns() {
		return DataUtil.asList(StringUtils.splitByWholeSeparator(this.url, ",")).stream()
				.filter(StringUtils::isNotBlank).map(StringUtils::trim).collect(Collectors.toList());
	}

}
