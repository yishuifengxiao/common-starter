package com.yishuifengxiao.common.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.properties.oauth2.ClientProperties;

/**
 * oauth2相关的配置
 * 
 * @author yishui
 * @date 2019年71月23日
 * @version 0.0.1
 */
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
	 * 所有不经过oauth2资源服务器授权管理的路径,value是不希望经过授权管理的路径，多个路径之间用半角逗号(,)分给开
	 */
	private Map<String, String> map = new HashMap<>();
	/**
	 * 所有内置的终端账号
	 */
	private List<ClientProperties> clients;

	public String getCheckTokenAccess() {
		return this.checkTokenAccess;
	}

	public void setCheckTokenAccess(String checkTokenAccess) {
		this.checkTokenAccess = checkTokenAccess;
	}

	public String getTokenKeyAccess() {
		return this.tokenKeyAccess;
	}

	public void setTokenKeyAccess(String tokenKeyAccess) {
		this.tokenKeyAccess = tokenKeyAccess;
	}

	public String getRealm() {
		return this.realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	/**
	 * 所有不经过oauth2资源服务器授权管理的路径,value是不希望经过授权管理的路径，多个路径之间用半角逗号(,)分给开
	 * 
	 * @return
	 */
	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	/**
	 * 获取所有不经过oauth2资源服务器授权管理的路径
	 * 
	 * @return
	 */
	public List<String> getExcludeUrls() {
		List<String> excludeUrls = new ArrayList<>();
		map.forEach((k, v) -> {
			if (StringUtils.isNotBlank(v)) {
				String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(v, ",");
				for (String url : urls) {
					excludeUrls.add(url);
				}

			}
		});
		return excludeUrls.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 获取所有内置的终端
	 * 
	 * @return
	 */
	public List<ClientProperties> getClients() {
		return clients;
	}

	/**
	 * 内置的终端
	 * 
	 * @param clients
	 */
	public void setClients(List<ClientProperties> clients) {
		this.clients = clients;
	}

}