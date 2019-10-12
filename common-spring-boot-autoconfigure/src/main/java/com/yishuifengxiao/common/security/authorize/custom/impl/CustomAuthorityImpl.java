package com.yishuifengxiao.common.security.authorize.custom.impl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.yishuifengxiao.common.security.authorize.custom.CustomAuthority;

/**
 * 自定义授权的默认实现
 * 
 * @author yishui
 * @date 2019年1月24日
 * @version 0.0.1
 */
public class CustomAuthorityImpl implements CustomAuthority {
	private final static Logger log = LoggerFactory.getLogger(CustomAuthorityImpl.class);

	@Override
	public boolean hasPermission(HttpServletRequest request, Authentication auth) {
		log.debug("【自定义授权】自定义授权的路径为 {}，认证信息为 {}  ", request.getRequestURI(), auth);

		return true;
	}

}
