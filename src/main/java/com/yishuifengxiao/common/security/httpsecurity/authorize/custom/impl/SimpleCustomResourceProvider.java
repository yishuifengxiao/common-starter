package com.yishuifengxiao.common.security.httpsecurity.authorize.custom.impl;

import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义授权的默认实现
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleCustomResourceProvider implements CustomResourceProvider {


	@Override
	public boolean hasPermission(HttpServletRequest request, Authentication auth) {
		log.debug("【自定义授权】自定义授权的路径为 {}，认证信息为 {}  ", request.getRequestURI(), auth);

		return true;
	}

	@Override
	public RequestMatcher requestMatcher() {
		return null;
	}

}
