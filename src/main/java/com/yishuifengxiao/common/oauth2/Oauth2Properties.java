package com.yishuifengxiao.common.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.tool.entity.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oauth2扩展支持属性配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
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

	/**
	 * 携带的basic token 无效时的提示信息
	 */
	private String invalidBasicToken = "无效的basic token";

	/**
	 * 终端信息不正确时的响应码，默认为500
	 */
	private Integer invalidClientCode = Response.Const.CODE_INTERNAL_SERVER_ERROR;

}