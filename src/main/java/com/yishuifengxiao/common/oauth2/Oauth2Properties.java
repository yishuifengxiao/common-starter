package com.yishuifengxiao.common.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oauth2相关的配置
 * 
 * @author yishui
 * @date 2019年71月23日
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.security.oauth2")
public class Oauth2Properties {

	/**
	 * Spring Security access rule for the check token endpoint (e.g. a SpEL
	 * expression like "isAuthenticated()") . Default is empty, which is interpreted
	 * as "denyAll()" (no access).
	 */
	private String checkTokenAccess;

	/**
	 * Spring Security access rule for the token key endpoint (e.g. a SpEL
	 * expression like "isAuthenticated()"). Default is empty, which is interpreted
	 * as "denyAll()" (no access).
	 */
	private String tokenKeyAccess;

	/**
	 * Realm name for client authentication. If an unauthenticated request comes in
	 * to the token endpoint, it will respond with a challenge including this name.
	 */
	private String realm = "yishuifengxiao";

	/**
	 * 终端不存在时异常提示信息
	 */
	private String clientNotExtis = "终端不存在";

	/**
	 * 终端对应的密码错误时的异常提示信息
	 */
	private String pwdErrorMsg = "终端密码错误";

}