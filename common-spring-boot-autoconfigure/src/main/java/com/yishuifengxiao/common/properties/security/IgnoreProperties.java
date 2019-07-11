/**
 * 
 */
package com.yishuifengxiao.common.properties.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * spring security忽视目录
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public class IgnoreProperties {
	/**
	 * 系统默认包含的静态路径
	 */
	private String[] staticResource = new String[] { "/js/**", "/css/**", "/images/**", "/fonts/**", "/**/**.png",
			"/**/**.jpg", "/**/**.html", "/**/**.ico", "/**/**.js", "/**/**.css", "/**/**.woff", "/**/**.ttf" };

	/**
	 * 系统默认包含的swagger-ui资源路径
	 */
	private String[] swaagerUiResource = new String[] { "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs" };
	/**
	 * 系统默认包含actuator相关的路径
	 */
	private String[] actuatorResource = new String[] { "/actuator/**" };
	/**
	 * 系统默认包含webjars相关的路径
	 */
	private String[] webjarsResource = new String[] { "/webjars/**" };
	/**
	 * 所有的资源
	 */
	private String[] allResources = new String[] { "/**" };

	/**
	 * 是否默认包含静态资源
	 */
	private Boolean containStaticResource = true;
	/**
	 * 是否包含swagger-ui的资源
	 */
	private Boolean containSwaagerUiResource = true;
	/**
	 * 是否包含actuator相关的路径
	 */
	private Boolean containActuator = true;
	/**
	 * 是否包含webJars资源
	 */
	private Boolean containWebjars = true;
	/**
	 * 是否包含所有的资源
	 */
	private Boolean containAll = false;

	/**
	 * 所有需要忽视的目录
	 */
	private Map<String, String> map = new HashMap<>();

	/**
	 * 以字符串形式获取到所有需要忽略的路径
	 * 
	 * @return
	 */
	public String getIgnoreString() {
		return Arrays.asList(this.getIgnore()).parallelStream().filter(t -> StringUtils.isNotBlank(t))
				.collect(Collectors.joining(", "));

	}

	/**
	 * 获取所有需要忽视的目录
	 * 
	 * @return
	 */
	public String[] getIgnore() {
		Set<String> set = new HashSet<>();
		if (this.containStaticResource) {
			set.addAll(Arrays.asList(staticResource));
		}
		if (this.containSwaagerUiResource) {
			set.addAll(Arrays.asList(swaagerUiResource));
		}
		if (this.containActuator) {
			set.addAll(Arrays.asList(actuatorResource));
		}
		if (this.containWebjars) {
			set.addAll(Arrays.asList(webjarsResource));
		}
		if (this.containAll) {
			set.addAll(Arrays.asList(allResources));
		}
		map.forEach((k, v) -> {
			if (StringUtils.isNotBlank(v)) {
				List<String> ignores = Arrays.asList(v.split(",")).parallelStream()
						.filter(t -> StringUtils.isNotBlank(t)).map(t -> t.trim()).collect(Collectors.toList());
				set.addAll(ignores);
			}
		});
		return set.toArray(new String[] {});
	}

	/**
	 * 是否默认包含静态资源，默认为包含
	 */
	public Boolean getContainStaticResource() {
		return containStaticResource;
	}

	public void setContainStaticResource(Boolean containStaticResource) {
		this.containStaticResource = containStaticResource;
	}

	/**
	 * 所有需要忽视的目录
	 */
	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String[] getStaticResource() {
		return staticResource;
	}

	public void setStaticResource(String[] staticResource) {
		this.staticResource = staticResource;
	}

	public String[] getSwaagerUiResource() {
		return swaagerUiResource;
	}

	public void setSwaagerUiResource(String[] swaagerUiResource) {
		this.swaagerUiResource = swaagerUiResource;
	}

	public String[] getActuatorResource() {
		return actuatorResource;
	}

	public void setActuatorResource(String[] actuatorResource) {
		this.actuatorResource = actuatorResource;
	}

	public String[] getWebjarsResource() {
		return webjarsResource;
	}

	public void setWebjarsResource(String[] webjarsResource) {
		this.webjarsResource = webjarsResource;
	}

	public Boolean getContainSwaagerUiResource() {
		return containSwaagerUiResource;
	}

	public void setContainSwaagerUiResource(Boolean containSwaagerUiResource) {
		this.containSwaagerUiResource = containSwaagerUiResource;
	}

	public Boolean getContainActuator() {
		return containActuator;
	}

	public void setContainActuator(Boolean containActuator) {
		this.containActuator = containActuator;
	}

	public Boolean getContainWebjars() {
		return containWebjars;
	}

	public void setContainWebjars(Boolean containWebjars) {
		this.containWebjars = containWebjars;
	}

	public String[] getAllResources() {
		return allResources;
	}

	public void setAllResources(String[] allResources) {
		this.allResources = allResources;
	}

	public Boolean getContainAll() {
		return containAll;
	}

	public void setContainAll(Boolean containAll) {
		this.containAll = containAll;
	}

}
