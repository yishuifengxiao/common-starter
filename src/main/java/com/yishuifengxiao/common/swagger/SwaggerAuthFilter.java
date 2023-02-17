package com.yishuifengxiao.common.swagger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.utils.HttpExtractor;

/**
 * <p>
 * swagger文档访问权限认证
 * </p>
 *
 * 在访问swagger页面时进行权限拦截,执行basic认证
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SwaggerAuthFilter implements Filter {

	private final static String[] PATTERNS = { "/v2/api-docs", "/swagger-resources" };

	private final AntPathMatcher matcher = new AntPathMatcher();

	private HttpExtractor httpExtractor = new HttpExtractor();

	private SwaggerProperties swaggerProperties;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		if (this.match(request)) {

			try {
				String[] tokens = httpExtractor.extractBasicAuth(request);
				if (null == tokens) {
					throw new CustomException("需要经过认证才能访问");
				}
				if (!StringUtils.equalsIgnoreCase(tokens[0], swaggerProperties.getUsername())) {
					throw new CustomException("用户名不正确");
				}
				if (!StringUtils.equalsIgnoreCase(tokens[1], swaggerProperties.getPassword())) {
					throw new CustomException("密码不正确");
				}

			} catch (Exception e) {
				// 允许跨域访问的域，可以是一个域的列表，也可以是通配符"*"
				response.setHeader("Access-Control-Allow-Origin", "*");
				// 允许使用的请求方法，以逗号隔开
				response.setHeader("Access-Control-Allow-Methods", "*");
				// 是否允许请求带有验证信息，
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setHeader("Cache-Control", "no-store");
				response.setDateHeader("Expires", 0);
				response.setHeader("WWW-authenticate", "Basic Realm=\"Please enter your user name and password\"");
				response.getWriter().write(e.getMessage());
				response.getWriter().flush();
				response.getWriter().close();
				return;
			}
		}

		chain.doFilter(request, response);

	}

	/**
	 * 当前请求是否需要经过请求认证
	 * 
	 * @param request 请求
	 * @return true表示需要认证，fals表示不需要
	 */
	private boolean match(HttpServletRequest request) {
		if (!StringUtils.isNoneBlank(swaggerProperties.getUsername(), swaggerProperties.getPassword())) {
			return false;
		}
		String uri = request.getRequestURI();
		if (StringUtils.isBlank(uri)) {
			return false;
		}

		for (String pattern : PATTERNS) {
			if (matcher.match(pattern, uri)) {
				return true;
			}
		}
		return false;

	}

	public SwaggerProperties getSwaggerProperties() {
		return swaggerProperties;
	}

	public void setSwaggerProperties(SwaggerProperties swaggerProperties) {
		this.swaggerProperties = swaggerProperties;
	}

}
