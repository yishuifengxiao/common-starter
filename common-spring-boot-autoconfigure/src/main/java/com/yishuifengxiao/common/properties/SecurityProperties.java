/**
 * 
 */
package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.constant.SecurityConstant;
import com.yishuifengxiao.common.properties.security.CoreProperties;
import com.yishuifengxiao.common.properties.security.CustomAuthProperties;
import com.yishuifengxiao.common.properties.security.HandlerProperties;
import com.yishuifengxiao.common.properties.security.IgnoreProperties;
import com.yishuifengxiao.common.properties.security.RemeberMeProperties;
import com.yishuifengxiao.common.properties.security.SessionProperties;
import com.yishuifengxiao.common.properties.security.ValidateProperties;
import com.yishuifengxiao.common.properties.security.social.QQProperties;

/**
 * 安全相关的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
@ConfigurationProperties(prefix = "yishuifengxiao.security")
public class SecurityProperties {

	/**
	 * 自定义处理器中成功的处理方式
	 */
	private HandlerProperties handler = new HandlerProperties();
	/**
	 * spring security 核心配置
	 */
	private CoreProperties core = new CoreProperties();
	/**
	 * spring security session相关的配置
	 */
	private SessionProperties session = new SessionProperties();

	/**
	 * 需要自定义权限的路径
	 */
	private CustomAuthProperties custom = new CustomAuthProperties();

	/**
	 * spring security 忽视目录配置
	 */
	private IgnoreProperties ignore = new IgnoreProperties();
	/**
	 * 记住我相关的属性
	 */
	private RemeberMeProperties remeberMe = new RemeberMeProperties();
	/**
	 * 验证码及短信登陆相关配置
	 */
	private ValidateProperties code = new ValidateProperties();
	/**
	 * QQ登陆相关的参数配置
	 */
	private QQProperties qq = new QQProperties();

	/**
	 * 加解密中需要使用的密钥
	 */
	private String secretKey;
	/**
	 * 关闭csrf功能,默认为true
	 */
	private Boolean closeCsrf = SecurityConstant.CLOSE_CSRF;
	/**
	 * 是否关闭cors保护，默认为false
	 */
	private Boolean closeCors = SecurityConstant.CLOSE_CORS;
	/**
	 * 是否开启httpBasic访问，默认为true
	 */
	private Boolean httpBasic = SecurityConstant.HTTP_BASIC;
	/**
	 * 资源名称,默认为yishuifengxiao
	 */
	private String realmName = SecurityConstant.REAL_NAME;

	/**
	 * 自定义处理器中成功的处理方式
	 */
	public HandlerProperties getHandler() {
		return handler;
	}

	public void setHandler(HandlerProperties handler) {
		this.handler = handler;
	}

	/**
	 * spring security 核心配置
	 */
	public CoreProperties getCore() {
		return core;
	}

	public void setCore(CoreProperties core) {
		this.core = core;
	}

	/**
	 * spring security session相关的配置
	 */
	public SessionProperties getSession() {
		return session;
	}

	public void setSession(SessionProperties session) {
		this.session = session;
	}

	/**
	 * 需要自定义权限的路径
	 */
	public CustomAuthProperties getCustom() {
		return custom;
	}

	public void setCustom(CustomAuthProperties custom) {
		this.custom = custom;
	}

	/**
	 * spring security 忽视目录配置
	 */
	public IgnoreProperties getIgnore() {
		return ignore;
	}

	public void setIgnore(IgnoreProperties ignore) {
		this.ignore = ignore;
	}

	/**
	 * 加解密中需要使用的密钥
	 */
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * 关闭csrf功能,默认为true
	 */
	public Boolean getCloseCsrf() {
		return closeCsrf;
	}

	public void setCloseCsrf(Boolean closeCsrf) {
		this.closeCsrf = closeCsrf;
	}

	/**
	 * 是否开启httpBasic访问，默认为true
	 */
	public Boolean getHttpBasic() {
		return httpBasic;
	}

	public void setHttpBasic(Boolean httpBasic) {
		this.httpBasic = httpBasic;
	}

	/**
	 * 资源名称,默认为yishuifengxiao
	 */
	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	/**
	 * 是否关闭cors保护，默认为false
	 */
	public Boolean getCloseCors() {
		return closeCors;
	}

	public void setCloseCors(Boolean closeCors) {
		this.closeCors = closeCors;
	}

	/**
	 * 记住我相关的属性
	 * 
	 * @return
	 */
	public RemeberMeProperties getRemeberMe() {
		return remeberMe;
	}

	public void setRemeberMe(RemeberMeProperties remeberMe) {
		this.remeberMe = remeberMe;
	}

	/**
	 * 验证码及短信登陆相关配置
	 * 
	 * @return
	 */
	public ValidateProperties getCode() {
		return code;
	}

	public void setCode(ValidateProperties code) {
		this.code = code;
	}

	/**
	 * QQ登陆相关的参数配置
	 * 
	 * @return
	 */
	public QQProperties getQq() {
		return qq;
	}

	public void setQq(QQProperties qq) {
		this.qq = qq;
	}

}
