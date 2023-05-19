package com.yishuifengxiao.common.security.utils;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<String, AntPathRequestMatcher> MAP = new HashMap<>();

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


        boolean anyMatch = MAP.values().stream().anyMatch(v -> v.matches(request));

        return !anyMatch;
    }

    /**
     * 获取路径匹配器
     *
     * @param pattern 匹配规则
     * @return AntPathRequestMatcher
     */
    private synchronized AntPathRequestMatcher getMatcher(String pattern) {
        // 存储的键
        StringBuilder key = new StringBuilder(pattern).append(httpMethod).append(caseSensitive);
        AntPathRequestMatcher matcher = MAP.get(key.toString());
        if (null == matcher) {
            matcher = new AntPathRequestMatcher(pattern, this.httpMethod, this.caseSensitive);
            MAP.put(key.toString(), matcher);
        }
        return matcher;
    }

}
