package com.yishuifengxiao.common.security.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>黑名单路径匹配器</p>
 * 如果路径在目标路径匹配范围之内,则返回为true,
 * <p>
 * 如 /demo/**表示除了/demo/**之外的路径都能匹配
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExcludeRequestMatcher implements RequestMatcher {

    private Set<RequestMatcher> requestMatchers = new HashSet<>();

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
        this.init();
    }


    @Override
    public boolean matches(HttpServletRequest request) {


        boolean anyMatch = requestMatchers.stream().anyMatch(v -> v.matches(request));

        return !anyMatch;
    }

    /**
     * 添加RequestMatcher
     *
     * @param requestMatcher 待添加的RequestMatcher
     * @return ExcludeRequestMatcher
     */
    public ExcludeRequestMatcher addRequestMatcher(RequestMatcher requestMatcher) {
        if (null != requestMatcher) {
            this.requestMatchers.add(requestMatcher);
        }
        return this;
    }


    private void init() {
        if (null != this.patterns) {
            this.requestMatchers =
                    this.patterns.parallelStream().filter(StringUtils::isNotBlank).distinct().map(pattern -> new AntPathRequestMatcher(pattern, this.httpMethod, this.caseSensitive)).collect(Collectors.toSet());
        }
    }
}
