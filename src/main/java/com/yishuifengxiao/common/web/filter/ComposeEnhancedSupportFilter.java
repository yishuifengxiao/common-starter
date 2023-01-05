package com.yishuifengxiao.common.web.filter;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.collections.SizeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 自定义过滤器组合器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ComposeEnhancedSupportFilter implements Filter {

    /**
     * 待执行的过滤器
     */
    private List<BaseFilter> filters;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SizeUtil.isEmpty(filters)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        for (BaseFilter filter : filters) {
            boolean match = filter.urlPatterns().parallelStream().anyMatch(v -> new AntPathRequestMatcher(v).matches(httpServletRequest));
            if (match) {
                filter.doFilter(request, response, chain);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 对过滤器进行处理，包括去除非法数据和进行排序
     *
     * @param filters 待处理的过滤器
     * @return 处理后的过滤器
     */
    private List<BaseFilter> filters(List<BaseFilter> filters) {
        if (null == filters) {
            this.filters = new ArrayList<>();
        }
        return filters.stream().filter(Objects::nonNull).filter(v -> SizeUtil.notEmpty(DataUtil.stream(v.urlPatterns())
                //
                .filter(StringUtils::isNotBlank).collect(Collectors.toSet()))).sorted((v1, v2) -> v2.order() - v1.order()).collect(Collectors.toList());
    }

    /**
     * 设置过滤器
     *
     * @param filters 过滤器
     */
    public void setFilters(List<BaseFilter> filters) {
        this.filters = filters(filters);
    }

    /**
     * 构造方法
     *
     * @param filters 过滤器
     */
    public ComposeEnhancedSupportFilter(List<BaseFilter> filters) {
        this.filters = filters(filters);
    }

    /**
     * 无参构造方法
     */
    public ComposeEnhancedSupportFilter() {
    }
}
