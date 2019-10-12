package com.yishuifengxiao.common.security.authorize.intercept;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.matcher.ExcludeRequestMatcher;

/**
 * 默认的资源管理配置
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class DefaultAuthorizeResourceProvider implements AuthorizeResourceProvider {

	private Oauth2Properties oauth2Properties;

	private SecurityProperties securityProperties;

	private SocialProperties socialProperties;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// 所有的路径都要经过授权
		http.requestMatcher(new ExcludeRequestMatcher(getExcludeUrls()));

	}

	/**
	 * 获取所有不经过oauth2管理的路径
	 * 
	 * @return
	 */
	private List<String> getExcludeUrls() {
		List<String> excludeUrls = Arrays.asList("/oauth/**", securityProperties.getHandler().getSuc().getRedirectUrl(), // 登录成功后跳转的地址
				socialProperties.getFilterProcessesUrl() + "/" + socialProperties.getQq().getProviderId(), // QQ登陆的地址
				socialProperties.getFilterProcessesUrl() + "/" + socialProperties.getWeixin().getProviderId(), // 微信登陆的地址
				socialProperties.getQq().getRegisterUrl(), // qq登陆成功后跳转的地址
				socialProperties.getWeixin().getRegisterUrl(), // 微信登陆成功后跳转的地址
				securityProperties.getCore().getLoginPage(), // 登陆页面的URL
				securityProperties.getCore().getFormActionUrl(), // 登陆页面表单提交地址
				securityProperties.getCore().getLoginOutUrl() // 退出页面
		);
		excludeUrls.addAll(oauth2Properties.getExcludeUrls());
		return excludeUrls.stream().distinct().collect(Collectors.toList());
	}

	public Oauth2Properties getOauth2Properties() {
		return oauth2Properties;
	}

	public void setOauth2Properties(Oauth2Properties oauth2Properties) {
		this.oauth2Properties = oauth2Properties;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public SocialProperties getSocialProperties() {
		return socialProperties;
	}

	public void setSocialProperties(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}
	
	

}
