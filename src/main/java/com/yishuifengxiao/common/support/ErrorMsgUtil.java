package com.yishuifengxiao.common.support;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.exception.ExceptionProperties;
import com.yishuifengxiao.common.tool.collections.EmptyUtil;

/**
 * 异常信息提示工具类
 * 
 * @author qingteng
 * @date 2020年11月2日
 * @version 1.0.0
 */
public class ErrorMsgUtil {

	/**
	 * 格式化后的异常存储信息
	 */
	private Map<String, String> errors = new WeakHashMap<>();

	private ExceptionProperties exceptionProperties;

	/**
	 * 获取所有的简单异常提示配置<br/>
	 * 剔除非法的配置信息，然后将所有的key(异常类的名字转换为小写形式)
	 * 
	 * @return
	 */
	public Map<String, String> getShortErrorMsg() {

		if (!this.errors.isEmpty()) {
			return this.errors;
		}

		this.errors.put("InsufficientAuthenticationException".toLowerCase(), "请求需要认证");
		this.errors.put("UsernameNotFoundException".toLowerCase(), "用户名不存在");
		this.errors.put("InvalidTokenException".toLowerCase(), "无效的访问令牌");
		this.errors.put("BadClientCredentialsException".toLowerCase(), "密码错误");
		this.errors.put("InvalidGrantException".toLowerCase(), "授权模方式无效");
		this.errors.put("InvalidClientException".toLowerCase(), "不存在对应的终端");
		this.errors.put("RedirectMismatchException".toLowerCase(), "请配置回调地址");
		this.errors.put("UnauthorizedClientException".toLowerCase(), "未获得本资源的访问授权");

		this.errors.put("ConstraintViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
		this.errors.put("DataIntegrityViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
		this.errors.put("DuplicateKeyException".toLowerCase(), "已经存在相似的数据,不能重复添加");

		if (null != this.exceptionProperties.getMap()) {
			this.exceptionProperties.getMap().forEach((k, v) -> {
				if (StringUtils.isNotBlank(k)) {
					this.errors.put(k.toLowerCase(), v);
				}
			});
		}
		return this.errors;
	}

	/**
	 * 根据异常从配置信息中获取提示信息<br/>
	 * 
	 * 提取过程如下：<br/>
	 * 1 先根据异常类的完整名字获取异常提示信息<br/>
	 * 2 如果第一步中没有获取异常信息，则根据异常类的名字(不区分大小)获取异常提示信息<br/>
	 * 3 如果还是没有获取到异常提示信息，且用户配置的提示信息不为空，则使用用户配置的第一个提示信息作为异常提示信息<br/>
	 * 4 如果还是没有获取到异常提示信息，就是用原来的异常类里的信息
	 * 
	 * @param e          异常信息
	 * @param defaultMsg 自定义提示信息
	 * @return 异常提示信息
	 */
	public String getErrorMsg(Throwable e, String... defaultMsg) {
		String msg = null;
		if (null != this.exceptionProperties.getFull()) {
			msg = this.exceptionProperties.getFull().get(e.getClass().getName());
		}
		if (StringUtils.isBlank(msg)) {
			msg = this.getShortErrorMsg().get(e.getClass().getSimpleName().toLowerCase());
		}
		if (StringUtils.isBlank(msg)) {
			return EmptyUtil.notEmpty(defaultMsg) ? defaultMsg[0] : e.getMessage();
		}
		return msg;
	}

	public ErrorMsgUtil() {

	}

	public ErrorMsgUtil(ExceptionProperties exceptionProperties) {

		this.exceptionProperties = exceptionProperties;
	}

	public ExceptionProperties getExceptionProperties() {
		return exceptionProperties;
	}

	public void setExceptionProperties(ExceptionProperties exceptionProperties) {
		this.exceptionProperties = exceptionProperties;
	}

}
