package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring social登陆相关的配置
 * 
 * @author yishui
 * @date 2019年7月14日
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.security.social")
public class SocialProperties {

	/**
	 * spring social中拦截的url的前缀 ，默认为 /auth
	 */
	private String filterProcessesUrl = "/auth";

	/**
	 * QQ登陆相关的属性配置
	 */
	private QqProperties qq = new QqProperties();

	/**
	 * 微信登陆相关的配置
	 */
	private WeixinProperties weixin = new WeixinProperties();

	/**
	 * 是否开启 spring social功能，默认为 false
	 */
	private Boolean enable = false;

	/**
	 * QQ登陆相关的属性配置
	 * 
	 * @return
	 */
	public QqProperties getQq() {
		return qq;
	}

	/**
	 * QQ登陆相关的属性配置
	 * 
	 * @param qq
	 */
	public void setQq(QqProperties qq) {
		this.qq = qq;
	}

	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}

	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	/**
	 * 微信登陆相关的配置
	 * 
	 * @return
	 */
	public WeixinProperties getWeixin() {
		return weixin;
	}

	public void setWeixin(WeixinProperties weixin) {
		this.weixin = weixin;
	}

	/**
	 * 是否开启 spring social功能，默认为 false
	 * 
	 * @return
	 */
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	/**
	 * QQ登陆相关的配置
	 * 
	 * @author yishui
	 * @date 2019年7月14日
	 * @version 1.0.0
	 */
	public static class QqProperties {

		private String appId;

		private String appSecret;

		private String registerUrl;

		/**
		 * 服务提供商id，默认为qq
		 */
		private String providerId = "qq";

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppSecret() {
			return appSecret;
		}

		public void setAppSecret(String appSecret) {
			this.appSecret = appSecret;
		}

		/**
		 * 服务提供商id，默认为qq
		 */
		public String getProviderId() {
			return providerId;
		}

		public void setProviderId(String providerId) {
			this.providerId = providerId;
		}

		public String getRegisterUrl() {
			return registerUrl;
		}

		public void setRegisterUrl(String registerUrl) {
			this.registerUrl = registerUrl;
		}

	}

	/**
	 * 微信登陆相关的配置
	 * 
	 * @author yishui
	 * @date 2019年7月16日
	 * @version 1.0.0
	 */
	public static class WeixinProperties {

		private String appId;

		private String appSecret;

		private String registerUrl;

		/**
		 * 服务提供商id，默认为weixin
		 */
		private String providerId = "weixin";

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppSecret() {
			return appSecret;
		}

		public void setAppSecret(String appSecret) {
			this.appSecret = appSecret;
		}

		/**
		 * 服务提供商id，默认为weixin
		 */
		public String getProviderId() {
			return providerId;
		}

		public void setProviderId(String providerId) {
			this.providerId = providerId;
		}

		public String getRegisterUrl() {
			return registerUrl;
		}

		public void setRegisterUrl(String registerUrl) {
			this.registerUrl = registerUrl;
		}

	}

}
