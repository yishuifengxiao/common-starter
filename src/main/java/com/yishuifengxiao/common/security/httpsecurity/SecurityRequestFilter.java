/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity;

import jakarta.servlet.Filter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * <p>
 * Security 请求过滤器
 * </P>
 * 所有该过滤器的子类的实例都会被组件自动收集然后注入到spring security过滤器链中
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityRequestFilter {

    OncePerRequestFilter filter();

    /**
     * 用于标明将该过滤器实例配置到spring security过滤器的那个点上
     *
     * @param http HttpSecurity
     * @throws Exception 配置时出现问题
     */
    void configure(Filter filter, HttpSecurity http) throws Exception;
}
