/**
 * 
 */
package com.yishuifengxiao.common.swagger.web.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.yishuifengxiao.common.service.AncestorService;

/**
 * 公共抽象控制器
 * 
 * @author yishui
 * @Date 2019年3月8日
 * @version 1.0.0
 */
public abstract class BaseController {
	/**
	 * 收集系统中所有的service
	 */
	@Autowired(required = false)
	protected Map<String, AncestorService> services;

	/**
	 * 对传入的参数进行非空处理
	 * 
	 * @param param
	 * @return
	 */
	protected <T> T convert(T t) {
		return t == null || "".equals(t) || "undefined".equals(t) ? null : t;
	}

	/**
	 * 将字符串转为Double
	 * 
	 * @param str
	 * @return
	 */
	protected Double convert2Double(String str) {
		if (StringUtils.isNumeric(str)) {
			return Double.parseDouble(str);
		}
		return null;
	}

	/**
	 * 将字符串转为 Long
	 * 
	 * @param str
	 * @return
	 */
	protected Long convert2Long(String str) {
		if (StringUtils.isNumeric(str)) {
			return Long.parseLong(str);
		}
		return null;
	}

	/**
	 * 将字符串的首字母变为小写的
	 * 
	 * @param s
	 *            字符串
	 * @return
	 */
	protected String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

	/**
	 * 根据service的名字获取service实例
	 * 
	 * @param serviceName
	 *            service的名字
	 * @return service实例
	 */
	@SuppressWarnings("unchecked")
	protected <T extends AncestorService> T service(String serviceName) {
		if (services == null) {
			return null;
		}
		AncestorService service = services.getOrDefault(toLowerCaseFirstOne(serviceName), null);
		return service == null ? null : (T) service;
	}

	/**
	 * 根据service的名字获取service实例
	 * 
	 * @param clazz
	 *            service的名字
	 * @return service实例
	 */
	protected <T extends AncestorService> T service(Class<T> clazz) {
		return service(clazz.getSimpleName());
	}

}
