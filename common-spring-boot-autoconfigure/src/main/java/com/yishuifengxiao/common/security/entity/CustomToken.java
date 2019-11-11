package com.yishuifengxiao.common.security.entity;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * 自定义授权接口
 * 
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1
 */
public class CustomToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5927640198972638101L;

	/**
	 * 登录用户的用户名
	 */
	private String username;

	/**
	 * 登录用户的 clientId
	 */
	private String clientId;

	/**
	 * 登录用户的角色
	 */
	private	Collection<GrantedAuthority> authorities;

	/**
	 * 当前token的授权模式
	 */
	private String grantType;
	/**
	 * token的生成时间
	 */
	private String date;
	/**
	 * token的过期时间
	 */
	private Integer expiresIn;

	/**
	 * 获取登录用户的用户名
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置登录用户的用户名
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取 登录用户的 clientId
	 * 
	 * @return
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * 设置 登录用户的 clientId
	 * 
	 * @param clientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


    /**
     * 获取 获取登录用户的角色
     * @return
     */
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}
   
	/**
	 * 设置 获取登录用户的角色
	 * @param authorities
	 */
	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	/**
	 * 获取当前token的授权模式
	 * 
	 * @return
	 */
	public String getGrantType() {
		return grantType;
	}

	/**
	 * 设置当前token的授权模式
	 * 
	 * @param grantType
	 */
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	/**
	 * 获取token的生成时间
	 * 
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * 设置token的生成时间
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * 获取token的过期时间
	 * 
	 * @return
	 */
	public Integer getExpiresIn() {
		return expiresIn;
	}

	/**
	 * 设置token的过期时间
	 * 
	 * @param expiresIn
	 */
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	public CustomToken(String username, String clientId, Collection<GrantedAuthority> authorities, String grantType) {
		this.username = username;
		this.clientId = clientId;
		this.authorities = authorities;
		this.grantType = grantType;
	}

	public CustomToken(String username, String clientId, Collection<GrantedAuthority> authorities, String grantType, String date,
			Integer expiresIn) {
		this.username = username;
		this.clientId = clientId;
		this.authorities = authorities;
		this.grantType = grantType;
		this.date = date;
		this.expiresIn = expiresIn;
	}

	public CustomToken() {

	}

	@Override
	public String toString() {
		return new StringBuffer("{\"username\":\"").append(username).append("\",\"clientId\":\"").append(clientId)
				.append("\",\"grantType\":\"").append(grantType).append("\",\"date\":\"").append(date)
				.append("\",\"expiresIn\":\"").append(expiresIn).append("\"}").toString();
	}

}
