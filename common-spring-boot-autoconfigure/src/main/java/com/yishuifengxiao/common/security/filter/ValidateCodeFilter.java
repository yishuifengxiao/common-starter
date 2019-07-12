package com.yishuifengxiao.common.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.eunm.CodeType;
import com.yishuifengxiao.common.validation.holder.CodeProcessorHolder;
import com.yishuifengxiao.common.validation.processor.CodeProcessor;

/**
 * 验证码过滤器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

	private final static Logger log = LoggerFactory.getLogger(ValidateCodeFilter.class);
	/**
	 * 自定义登陆失败后处理器
	 */
	private AuthenticationFailureHandler authenticationFailureHandler;

	/**
	 * 存放所有需要校验验证码的url【即表明什么样的URL需要用到什么样的验证码】
	 */
	private Map<String, CodeType> urlMap = new HashMap<>();

	private CodeProcessorHolder codeProcessorHolder;

	/**
	 * 用于定义路由规则，因为下面的路径里有统配符，验证请求的URL与配置的URL是否匹配的类
	 */
	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	private SecurityProperties securityProperties;

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();

		// 需要拦截的路径
		securityProperties.getCode().getFilter().forEach((codeType, urls) -> {
			addUrlTpMap(urls, CodeType.parse(codeType));
		});
	}

	/**
	 * 将系统需要校验验证码的URL根据校验的类型放入map中
	 * 
	 * @param urlString        需要校验验证码的URL
	 * @param validateCodeType 验证码类型
	 */
	protected void addUrlTpMap(String urlString, CodeType validateCodeType) {
		if (StringUtils.isNotBlank(urlString) && validateCodeType != null) {
			String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString, ",");
			for (String url : urls) {
				urlMap.put(url, validateCodeType);
			}
		}

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		CodeType validateCodeType = getValidateCodeType(request);
		if (validateCodeType != null) {

			try {
				CodeProcessor validateCodeProcessor = codeProcessorHolder.findValidateCodeProcessor(validateCodeType);
				log.info("---------------------------------- 请求校验{}中的验证码的的类型是 {} ,校验器类型为 {}", request.getRequestURI(),
						validateCodeType, validateCodeProcessor);
				validateCodeProcessor.validate(new ServletWebRequest(request, response));
			} catch (ValidateException e) {
				log.debug("验证码验证校验未通过，出现问题 {}", e.getMessage());
				authenticationFailureHandler.onAuthenticationFailure(request, response,
						new InsufficientAuthenticationException(e.getMessage()));
				// 失败后不执行后面的过滤器
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * 获取校验码的类型，如果当前请求不需要校验，则返回null
	 * 
	 * @param request
	 * @return
	 */
	private CodeType getValidateCodeType(HttpServletRequest request) {
		CodeType result = null;
		log.debug("=================> 获取校验码类型时的URL为 {}，请求类型为 {}", request.getRequestURI(), request.getMethod());
		if (!securityProperties.getCode().getIsFilterGet()
				&& StringUtils.equalsIgnoreCase(request.getMethod(), "get")) {
			return null;
		}
		// 根据请求url获取拦截器类型
		Set<String> urls = urlMap.keySet();
		for (String url : urls) {
			if (antPathMatcher.match(url, request.getRequestURI())) {
				result = urlMap.get(url);
				break;
			}
		}

		return result;
	}

	public AuthenticationFailureHandler getAuthenticationFailureHandler() {
		return authenticationFailureHandler;
	}

	public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
		this.authenticationFailureHandler = authenticationFailureHandler;
	}

	public CodeProcessorHolder getCodeProcessorHolder() {
		return codeProcessorHolder;
	}

	public void setCodeProcessorHolder(CodeProcessorHolder codeProcessorHolder) {
		this.codeProcessorHolder = codeProcessorHolder;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public ValidateCodeFilter() {

	}

	public ValidateCodeFilter(AuthenticationFailureHandler authenticationFailureHandler,
			CodeProcessorHolder codeProcessorHolder, SecurityProperties securityProperties) {
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.codeProcessorHolder = codeProcessorHolder;
		this.securityProperties = securityProperties;
	}

}