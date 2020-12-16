package com.yishuifengxiao.common.code.extractor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.tool.context.SessionStorage;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认的验证码信息提取器
 * 
 * @author qingteng
 * @date 2020年11月7日
 * @version 1.0.0
 */
@Slf4j
public class SimpleCodeExtractor implements CodeExtractor {

	@Override
	public String extractKey(HttpServletRequest request, String key) {

		return this.extracted(request, key);
	}

	@Override
	public String extractValue(HttpServletRequest request, String key) {
		return this.extracted(request, key);
	}

	/**
	 * 从请求中根据key获取对应的信息
	 * 
	 * @param request HttpServletRequest
	 * @param key     获取对应的信息的key
	 * @return
	 */
	private String extracted(HttpServletRequest request, String key) {

		// 先从请求头中获取
		String value = request.getHeader(key);

		if (StringUtils.isBlank(value)) {
			// 如果没有获取到就从请求参数里获取
			value = request.getParameter(key);
		}

		try {
			if (StringUtils.isBlank(value)) {
				// 如果还是没有，就从请求上下文获取
				key = (String) SessionStorage.get(key);
			}

			if (StringUtils.isBlank(value)) {
				// 如果还是没有，就从session中获取
				value = (String) request.getSession().getAttribute(key);
			}
		} catch (Exception e) {
			log.debug("【易水组件】验证码信息提取器中提取信息{}对应的值时出现问题，出现问题的原因为 {}", e.getMessage());
		}

		if (StringUtils.isBlank(value)) {
			// 如果还是没有，就从请求cookie里获取
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (StringUtils.equalsIgnoreCase(key, cookie.getName())) {
						value = cookie.getValue();
						break;
					}
				}
			}
		}
		return value;
	}

}
