package com.yishuifengxiao.common.support;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.web.WebExceptionProperties;

/**
 * 异常信息提取工具
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ErrorUtil {

	/**
	 * 格式化后的异常存储信息
	 */
	private static final Map<String, String> ERRORS = new WeakHashMap<>();

	/**
	 * <p>
	 * 根据异常从配置信息中获取异常信息
	 * </p>
	 * 
	 * 提取过程如下
	 * <ul>
	 * <li>先根据异常类的完整名字获取异常提示信息</li>
	 * <li>如果第一步中没有获取异常信息，则根据异常类的名字(不区分大小)获取异常提示信息</li>
	 * <li>如果还是没有获取到异常提示信息，且用户配置的提示信息不为空，则使用用户配置的第一个提示信息作为异常提示信息</li>
	 * <li>如果还是没有获取到异常提示信息，就是用原来的异常类里的信息</li>
	 * </ul>
	 * 
	 * @param e          异常信息
	 * @param defaultMsg 自定义提示信息
	 * @return 异常提示信息
	 */
	public static String getErrorMsg(Throwable e, String defaultMsg) {
		if (null == e) {
			return "未知异常";
		}
		// 全称信息提示
		String msg = ERRORS.get(e.getClass().getName());

		// 根据简称查找
		if (StringUtils.isBlank(msg)) {
			msg = ERRORS.get(e.getClass().getSimpleName().toLowerCase());
		}

		// 返回默认信息
		if (StringUtils.isBlank(msg)) {
			return StringUtils.isNotBlank(defaultMsg) ? defaultMsg : e.getMessage();
		}
		return msg;
	}

	/**
	 * <p>
	 * 根据异常从配置信息中获取异常信息
	 * </p>
	 * 
	 * 提取过程如下
	 * <ul>
	 * <li>先根据异常类的完整名字获取异常提示信息</li>
	 * <li>如果第一步中没有获取异常信息，则根据异常类的名字(不区分大小)获取异常提示信息</li>
	 * <li>如果还是没有获取到异常提示信息，就是用原来的异常类里的信息</li>
	 * </ul>
	 * 
	 * @param e 异常信息
	 * @return 异常提示信息
	 */
	public static String getErrorMsg(Throwable e) {
		return getErrorMsg(e, null);
	}

	/**
	 * 初始化数据
	 * 
	 * @param exceptionProperties 全局异常捕获属性配置
	 */
	public void init(WebExceptionProperties exceptionProperties) {

		ErrorUtil.ERRORS.put("InsufficientAuthenticationException".toLowerCase(), "请求需要认证");
		ErrorUtil.ERRORS.put("UsernameNotFoundException".toLowerCase(), "用户名不存在");
		ErrorUtil.ERRORS.put("InvalidTokenException".toLowerCase(), "无效的访问令牌");
		ErrorUtil.ERRORS.put("BadClientCredentialsException".toLowerCase(), "密码错误");
		ErrorUtil.ERRORS.put("InvalidGrantException".toLowerCase(), "授权模方式无效");
		ErrorUtil.ERRORS.put("InvalidClientException".toLowerCase(), "不存在对应的终端");
		ErrorUtil.ERRORS.put("RedirectMismatchException".toLowerCase(), "请配置回调地址");
		ErrorUtil.ERRORS.put("UnauthorizedClientException".toLowerCase(), "未获得本资源的访问授权");

		ErrorUtil.ERRORS.put("ConstraintViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
		ErrorUtil.ERRORS.put("DataIntegrityViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
		ErrorUtil.ERRORS.put("DuplicateKeyException".toLowerCase(), "已经存在相似的数据,不能重复添加");
		// 配置简称名字匹配的提示信息
		if (null != exceptionProperties.getMap()) {
			exceptionProperties.getMap().forEach((k, v) -> {
				if (StringUtils.isNoneBlank(k, v)) {
					ErrorUtil.ERRORS.put(k.toLowerCase(), v);
				}
			});
		}
		// 配置全称名字匹配的提示信息
		if (null != exceptionProperties.getFull()) {
			exceptionProperties.getFull().forEach((k, v) -> {
				if (StringUtils.isNoneBlank(k, v)) {
					ErrorUtil.ERRORS.put(k.toLowerCase(), v);
				}
			});
		}
	}

}
