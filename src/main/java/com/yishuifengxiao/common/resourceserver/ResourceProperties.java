package com.yishuifengxiao.common.resourceserver;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.security.constant.UriConstant;
import com.yishuifengxiao.common.security.eunm.ReturnType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源服务器相关属性配置
 * 
 * @author yishui
 * @version 1.0.0
 * @date 2019-10-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.security.resourceserver")
public class ResourceProperties {
	/**
	 * 是否开启资源服务器功能，默认为false
	 */
	private Boolean enable = false;
	/**
	 * 指向认证服务器里token校验地址,一般默认的uri为/oauth/check_token
	 */
	private String tokenCheckUrl;

	/**
	 * 出现异常时的处理方式
	 */
	private ReturnType returnType = ReturnType.JSON;

	/**
	 * 默认的重定向URL，默认为主页地址 /
	 */
	private String redirectUrl = UriConstant.DEFAULT_HOME_URL;

}
