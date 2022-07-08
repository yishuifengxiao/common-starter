/**
 * 
 */
package com.yishuifengxiao.common.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义跨域支持
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class CustomCorsFilter implements Filter {

	private CorsProperties corsProperties;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, corsProperties.getAllowedOrigins());
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, corsProperties.getAllowedHeaders());
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
					corsProperties.getAllowCredentials() + "");
			httpServletResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, corsProperties.getAllowedMethods());
			corsProperties.getHeaders().forEach((k, v) -> {
				if (StringUtils.isNoneBlank(k, v)) {
					httpServletResponse.addHeader(k, v);
				}
			});
		} catch (Exception e) {
			if (e instanceof IOException || e instanceof ServletException) {
				throw e;
			}
			if (log.isInfoEnabled()) {
				log.info("[unkown] 跨域支持捕获到未知异常 {}", e.getMessage());
			}

		}

		chain.doFilter(request, response);
	}

	public CustomCorsFilter(CorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

}
