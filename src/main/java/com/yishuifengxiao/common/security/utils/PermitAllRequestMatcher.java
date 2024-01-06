package com.yishuifengxiao.common.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 允许通过资源的RequestMatcher
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/6 21:40
 * @since 1.0.0
 */
public class PermitAllRequestMatcher implements RequestMatcher {

    private Collection<String> patterns;

    private Collection<String> suffixes;


    @Override
    public boolean matches(HttpServletRequest request) {
        patterns = null == patterns ? Collections.emptyList() : patterns.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        suffixes = null == suffixes ? Collections.emptyList() : suffixes.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());

        List<RequestMatcher> requestMatchers = new ArrayList<>();
        patterns.stream().map(AntPathRequestMatcher::new).forEach(requestMatchers::add);

        if (!suffixes.isEmpty()) {
            requestMatchers.add(httpServletRequest -> {
                String suffix = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), ".");
                if (StringUtils.isBlank(suffix)) {
                    return false;
                }
                return suffixes.stream().anyMatch((v) -> StringUtils.equalsIgnoreCase(v, suffix));
            });
        }

        return new OrRequestMatcher(requestMatchers).matches(request);
    }

    public PermitAllRequestMatcher(Collection<String> patterns, Collection<String> suffixes) {
        this.patterns = patterns;
        this.suffixes = suffixes;
    }

    public PermitAllRequestMatcher() {
    }

    public void setPatterns(Collection<String> patterns) {
        this.patterns = patterns;
    }

    public void setSuffixes(Collection<String> suffixes) {
        this.suffixes = suffixes;
    }

    public Collection<String> getPatterns() {
        return patterns;
    }

    public Collection<String> getSuffixes() {
        return suffixes;
    }
}
