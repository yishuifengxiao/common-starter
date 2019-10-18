package com.yishuifengxiao.common.security.context;

import org.springframework.util.Assert;

/**
 * 自定义异常信息存储类
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class SecurityHolder {

	private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

	public static void clearContext() {
		contextHolder.remove();
	}

	public static SecurityContext getContext() {
		SecurityContext ctx = contextHolder.get();

		if (ctx == null) {
			ctx = createEmptyContext();
			contextHolder.set(ctx);
		}

		return ctx;
	}

	public static void setContext(SecurityContext context) {
		Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
		contextHolder.set(context);
	}

	public static SecurityContext createEmptyContext() {
		return new SecurityContextImpl();
	}
}
