package com.yishuifengxiao.common.security.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.properties.oauth2.ClientProperties;

public abstract class AbstractClientDetailsService implements ClientDetailsService {

	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected Oauth2Properties oauth2Properties;

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		// @formatter:off
		/*
		 * 
		 * clientDetails.setClientId("client");
		 * clientDetails.setClientSecret(passwordEncoder.encode("123456"));
		 * clientDetails.setAuthorizedGrantTypes(new
		 * HashSet<String>(Arrays.asList("password", "authorization_code",
		 * "refresh_token", "implicit","client_credentials")));
		 * clientDetails.setRegisteredRedirectUri(new
		 * HashSet<String>(Arrays.asList("http://localhost:8888/login")));
		 * clientDetails.setAuthorities(AuthorityUtils
		 * .createAuthorityList("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT"));
		 * clientDetails.setScope(Arrays.asList("read", "write", "trust"));
		 * clientDetails.setRefreshTokenValiditySeconds(6000);
		 * clientDetails.setAccessTokenValiditySeconds(600);
		 * 
		 */
		// @formatter:on

		ClientDetails clientDetails = getDefaultClientDetail(clientId);
		if (clientDetails == null) {
			clientDetails = findClientByClientId(clientId);
		}
		return clientDetails;
	}

	/**
	 * 获取用户设置的终端
	 * 
	 * @param clientId
	 * @return
	 * @throws ClientRegistrationException
	 */
	public abstract ClientDetails findClientByClientId(String clientId) throws ClientRegistrationException;

	/**
	 * 获取内置的默认终端
	 * 
	 * @param clientId
	 * @return
	 */
	private ClientDetails getDefaultClientDetail(String clientId) {

		ClientProperties clientProperty = getClientProperties(clientId);
		if (clientProperty != null) {
			// 查询出客户端
			BaseClientDetails clientDetails = new BaseClientDetails();

			// @formatter:off

			// 用于唯一标识每一个客户端(client); 在注册时必须填写(也可由服务端自动生成).
			// 对于不同的grant_type,该字段都是必须的. 在实际应用中的另一个名称叫appKey,与client_id是同一个概念.
			clientDetails.setClientId(clientId);

			// 用于指定客户端(client)的访问密匙; 在注册时必须填写(也可由服务端自动生成).
			// 对于不同的grant_type,该字段都是必须的. 在实际应用中的另一个名称叫appSecret,与client_secret是同一个概念.
			clientDetails.setClientSecret(passwordEncoder.encode(StringUtils.isNotBlank(clientProperty.getClientSecret())?clientProperty.getClientSecret():clientId));// 这里是加密后的密码

			// 指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials,
			// 若支持多个grant_type用逗号(,)分隔,如: "authorization_code,password".
			// 在实际应用中,当注册时,该字段是一般由服务器端指定的,而不是由申请者去选择的,最常用的grant_type组合有:
			// "authorization_code,refresh_token"(针对通过浏览器访问的客户端);
			// "password,refresh_token"(针对移动设备的客户端).
			// implicit与client_credentials在实际中很少使用.
			clientDetails.setAuthorizedGrantTypes(
					new HashSet<String>(Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(clientProperty.getGrantType(),
							","))));

			// 客户端的重定向URI,可为空, 当grant_type为authorization_code或implicit时,
			// 在Oauth的流程中会使用并检查与注册时填写的redirect_uri是否一致
		
			
			
			clientDetails.setRegisteredRedirectUri(
					new HashSet<String>(Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(clientProperty.getRegisteredRedirectUris(),
							","))));

			// 指定客户端所拥有的Spring Security的权限值,可选, 若有多个权限值,用逗号(,)分隔, 如: "ROLE_UNITY,ROLE_USER".
			// 对于是否要设置该字段的值,要根据不同的grant_type来判断,
			// 若客户端在Oauth流程中需要用户的用户名(username)与密码(password)的(authorization_code,password),
			// 则该字段可以不需要设置值,因为服务端将根据用户在服务端所拥有的权限来判断是否有权限访问对应的API.
			// 但如果客户端在Oauth流程中不需要用户信息的(implicit,client_credentials),
			// 则该字段必须要设置对应的权限值, 因为服务端将根据该字段值的权限来判断是否有权限访问对应的API.
			//clientDetails.setAuthorities(AuthorityUtils.createAuthorityList(
			//		StringUtils.isBlank(client.getAuthorities()) ? DEFAULT_AUTHORITY : client.getAuthorities()));
			clientDetails.setAuthorities(AuthorityUtils.createAuthorityList(clientProperty.getAuthorities()));

			// 指定客户端申请的权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔,如: "read,write".
			// scope的值与security.xml中配置的‹intercept-url的access属性有关系. 如‹intercept-url的配置为
			// ‹intercept-url pattern="/m/**" access="ROLE_MOBILE,SCOPE_READ"/>
			// 则说明访问该URL时的客户端必须有read权限范围. write的配置值为SCOPE_WRITE, trust的配置值为SCOPE_TRUST.
			// 在实际应该中, 该值一般由服务端指定, 常用的值为read,write.
			clientDetails.setScope(Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(clientProperty.getScopes(),",")));

			// 设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false', 'read','write'.
			// 该字段只适用于grant_type="authorization_code"的情况,当用户登录成功后,若该值为'true'或支持的scope值,则会跳过用户Approve的页面,
			// 直接授权.
			// 该字段与 trusted 有类似的功能, 是 spring-security-oauth2 的 2.0 版本后添加的新属性.
			clientDetails.setAutoApproveScopes(new HashSet<String>(Arrays.asList(clientProperty.getAutoApproveScopes())));

			// 设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).
			// 若客户端的grant_type不包括refresh_token,则不用关心该字段 在项目中,
			// 可具体参考DefaultTokenServices.java中属性refreshTokenValiditySeconds.

			// 在实际应用中, 该值一般是由服务端处理的, 不需要客户端自定义.
			clientDetails.setRefreshTokenValiditySeconds(clientProperty.getRefreshTokenValiditySeconds());

			// 设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).
			// 在服务端获取的access_token JSON数据中的expires_in字段的值即为当前access_token的有效时间值.
			// 在项目中, 可具体参考DefaultTokenServices.java中属性accessTokenValiditySeconds
			clientDetails.setAccessTokenValiditySeconds(clientProperty.getAccessTokenValiditySeconds());
			
			return clientDetails; 
		}
		return null;

	}

	/**
	 * 获取一个内置的终端
	 * 
	 * @param clientId
	 * @return
	 */
	private ClientProperties getClientProperties(String clientId) {
		List<ClientProperties> clientProperties = oauth2Properties.getClients();
		if (clientProperties != null) {
			for (ClientProperties clientPropertie : clientProperties) {
				if (StringUtils.equals(clientId, clientPropertie.getClientId())) {
					return clientPropertie;
				}
			}
		}

		return null;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public Oauth2Properties getOauth2Properties() {
		return oauth2Properties;
	}

	public void setOauth2Properties(Oauth2Properties oauth2Properties) {
		this.oauth2Properties = oauth2Properties;
	}

}