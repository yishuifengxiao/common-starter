package com.yishuifengxiao.common.security.matcher;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 排除路径匹配器<br/>
 * 如果路径在目标路径匹配范围之内,则返回为true<br/>
 * 
 * 如 /demo/**表示除了/demo/**之外的路径都能匹配
 * 
 * @author yishui
 * @date 2019年7月22日
 * @version 1.0.0
 */
public class ExcludeRequestMatcher implements RequestMatcher {

	private String httpMethod;
	private boolean caseSensitive;

	private List<String> patterns = new ArrayList<>();

	public ExcludeRequestMatcher(List<String> patterns) {
		this(null, true, patterns);
	}

	public ExcludeRequestMatcher(String httpMethod, List<String> patterns) {
		this(httpMethod, true, patterns);
	}

	public ExcludeRequestMatcher(String httpMethod, boolean caseSensitive, List<String> patterns) {
		if (patterns == null) {
			throw new IllegalArgumentException("匹配路径不能为空");
		}
		this.httpMethod = httpMethod;
		this.caseSensitive = caseSensitive;
		this.patterns = patterns;
	}

	@Override
	public boolean matches(HttpServletRequest request) {

		boolean matche = true;

		for (String pattern : this.patterns) {
			if (StringUtils.isBlank(pattern)) {
				continue;
			}
			if (new AntPathRequestMatcher(pattern, this.httpMethod, this.caseSensitive).matches(request)) {
				matche = false;
				break;
			}
		}

		return matche;
	}

}
