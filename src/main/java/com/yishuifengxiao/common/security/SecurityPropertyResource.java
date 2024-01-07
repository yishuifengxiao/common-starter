/**
 *
 */
package com.yishuifengxiao.common.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * <p>资源管理器</p>
 * <p>管理系统中所有的资源</p>
 * <p>
 * 定义参见 <a href="https://springdoc.cn/spring-security/servlet/authorization/authorize-http-requests.html">https://springdoc.cn/spring-security/servlet/authorization/authorize-http-requests.html</a>
 * </p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityPropertyResource {

    /**
     * spring security 相关的配置
     *
     * @return spring security 相关的配置
     */
    SecurityProperties security();

    /**
     * permitAll - 该请求不需要授权，是一个公共端点；请注意，在这种情况下，永远不会从 session 中检索 Authentication
     *
     * @return 不需要授权的请求资源
     */
    RequestMatcher permitAll();

    /**
     * anonymous - 该请求允许匿名访问，anonymous() 用于只允许匿名用户访问的公开内容
     *
     * @return 许匿名访问的请求资源
     */
    RequestMatcher anonymous();


    /**
     * 是否显示加载细节
     *
     * @return true表示显示加载细节，false不显示
     */
    boolean showDetail();

    /**
     * 获取项目的context-path
     *
     * @return 项目的context-path,若未配置则返回为空字符串""
     */
    String contextPath();


}
