package com.yishuifengxiao.common.properties.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义权限的配置文件
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public class CustomAuthProperties {

	/**
	 * 需要自定义授权的路径 <br/>
	 * 键 : 自定义，不参与匹配 <br/>
	 * 值 ：需要匹配的路径，多个路径之间用半角逗号(,)隔开
	 */
	private Map<String, String> map = new HashMap<>();

	/**
	 * 获取所有需要设置自定义权限的路径
	 * 
	 * @return
	 */
	public Map<String, String> getMap() {
		return map;
	}

	/**
	 * 设置所有需要设置自定义权限的路径
	 * 
	 * @return
	 */
	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	/**
	 * 获取所有自定义权限的路径
	 * @return
	 */
	public List<String> getAll() {
		List<String> list = new ArrayList<>();
		map.forEach((k, v) -> {
			if (StringUtils.isNotBlank(v)) {
				List<String> customs = Arrays.asList(v.split(",")).parallelStream()
						.filter(t -> StringUtils.isNotBlank(t)).map(t -> t.trim()).collect(Collectors.toList());
				if (customs != null && customs.size() > 0) {
					list.addAll(customs);
				}
			}

		});

		return list;
	}

}
