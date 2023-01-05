package com.yishuifengxiao.common.web.filter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.Collections;

/**
 * 过滤器基础接口+
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface BaseFilter extends Filter {

    String MATCH_ALL = "/**";

    /**
     * 执行的顺序
     *
     * @return 执行的顺序，数字越大越先执行
     */
    int order();

    /**
     * <p>拦截的路径</p>
     * <p>这里采用的是<code>AntPathRequestMatcher</code>进行路径匹配。在此模式下 /** 表示匹配所属路径</p>
     * <p>Using a pattern value of /** or ** is treated as a universal match,
     * which will match any request. Patterns which end with /** (and have no other wildcards)
     * are optimized by using a substring match — a pattern
     * of /aaa/** will match /aaa, /aaa/ and any sub-directories, such as /aaa/bbb/ccc.</p>
     *
     * @return 拦截的路径，若为空则表示该过滤失效
     * @see AntPathRequestMatcher
     */
    default Collection<String> urlPatterns() {
        return Collections.singletonList(MATCH_ALL);
    }
}
