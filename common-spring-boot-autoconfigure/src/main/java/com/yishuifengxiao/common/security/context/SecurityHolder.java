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

	private static final ThreadLocal<SecurityContext> CONTEXT_HOLLDER = new ThreadLocal<>();

	public static void clearContext() {
		CONTEXT_HOLLDER.remove();
	}

	public static SecurityContext getContext() {
		SecurityContext ctx = CONTEXT_HOLLDER.get();

		if (ctx == null) {
			ctx = createEmptyContext();
			CONTEXT_HOLLDER.set(ctx);
		}

		return ctx;
	}

	public static void setContext(SecurityContext context) {
		Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
		CONTEXT_HOLLDER.set(context);
	}

	public static SecurityContext createEmptyContext() {
		return new SecurityContextImpl();
	}
}
