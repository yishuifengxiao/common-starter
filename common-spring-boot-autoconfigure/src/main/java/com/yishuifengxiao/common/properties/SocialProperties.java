package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.properties.social.QqProperties;

/**
 * spring social登陆相关的配置
 * 
 * @author yishui
 * @date 2019年7月14日
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.social")
public class SocialProperties {

	/**
	 * QQ登陆相关的属性配置
	 */
	private QqProperties qq = new QqProperties();

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

}
