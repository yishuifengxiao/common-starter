package com.yishuifengxiao.common.security.entity;

import java.io.Serializable;

/**
 * 用户实体信息
 * 
 * @author yishui
 * @date 2019年10月11日
 * @version 1.0.0
 */
public class TokenInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5919096605580285824L;

	private String username;

	private String clientId;

	private String sessionId;

	private String ip;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public TokenInfo() {

	}

	public TokenInfo(String username, String clientId, String sessionId) {
		this.username = username;
		this.clientId = clientId;
		this.sessionId = sessionId;
	}

	public TokenInfo(String username, String clientId, String sessionId, String ip) {

		this.username = username;
		this.clientId = clientId;
		this.sessionId = sessionId;
		this.ip = ip;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TokenInfo [username=");
		builder.append(username);
		builder.append(", clientId=");
		builder.append(clientId);
		builder.append(", sessionId=");
		builder.append(sessionId);
		builder.append(", ip=");
		builder.append(ip);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;}
		if (obj == null) {
			return false;}
		if (getClass() != obj.getClass()) {
			return false;}
		TokenInfo other = (TokenInfo) obj;
		if (clientId == null) {
			if (other.clientId != null) {
				return false;}
		} else if (!clientId.equals(other.clientId)) {
			return false;}
		if (ip == null) {
			if (other.ip != null) {
				return false;}
		} else if (!ip.equals(other.ip)) {
			return false;}
		if (sessionId == null) {
			if (other.sessionId != null) {
				return false;}
		} else if (!sessionId.equals(other.sessionId)) {
			return false;}
		if (username == null) {
			if (other.username != null) {
				return false;}
		} else if (!username.equals(other.username)) {
			return false;}
		return true;
	}

}
