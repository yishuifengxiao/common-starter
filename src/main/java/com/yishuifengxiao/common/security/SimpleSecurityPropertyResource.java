/**
 *
 */
package com.yishuifengxiao.common.security;

import com.yishuifengxiao.common.security.constant.UriConstant;
import com.yishuifengxiao.common.security.support.AbstractSecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.utils.PermitAllRequestMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 简单实现的资源管理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleSecurityPropertyResource implements SecurityPropertyResource {

    /**
     * 系统默认包含的静态路径
     */
    private static String[] STATIC_RESOURCE = new String[]{"html", "jpg", "png", "jpeg", "css", "js", "html", "ico",
            "woff", "ttf", "svg"};


    /**
     * 系统默认包含的swagger-ui资源路径
     */
    private static String[] SWAGGER_UI_RESOURCE = new String[]{"/swagger-ui.html", "/swagger-resources/**", "/swagger" +
            "-ui/**", "/v3/api-docs/swagger-config", "/v3/api-docs/default"};
    /**
     * 系统默认包含actuator相关的路径
     */
    private static String[] ACTUATOR_RESOURCE = new String[]{"/actuator/**"};
    /**
     * 系统默认包含webjars相关的路径
     */
    private static String[] WEBJARS_RESOURCE = new String[]{"/webjars/**"};


    /**
     * spring security 属性配置文件
     */
    private SecurityProperties securityProperties;


    private String contextPath;

    @Override
    public SecurityProperties security() {
        return this.securityProperties;
    }

    @Override
    public RequestMatcher permitAll() {

        // 获取配置的资源
        Set<String> urls = this.getUrls(this.securityProperties.getResource().getPermits());
        // 需要增加的资源
        urls.addAll(Arrays.asList(UriConstant.ERROR_PAGE,
                // 权限拦截时默认的跳转地址
                securityProperties.getRedirectUrl(),
                // 登陆页面的URL
                securityProperties.getLoginPage(),
                // session失效时跳转的地址
                securityProperties.getSession().getSessionInvalidUrl()

        ));
        if (this.securityProperties.getResource().getPermitWebjars()) {
            urls.addAll(Arrays.asList(WEBJARS_RESOURCE));
        }
        if (this.securityProperties.getResource().getPermitSwaggerUiResource()) {
            urls.addAll(Arrays.asList(SWAGGER_UI_RESOURCE));
        }
        if (this.securityProperties.getResource().getPermitActuator()) {
            urls.addAll(Arrays.asList(ACTUATOR_RESOURCE));
        }

        if (this.securityProperties.getResource().getPermitErrorPage()) {
            // 错误页面
            urls.add(UriConstant.ERROR_PAGE);
        }
        urls = urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        //security全局增强元数据
        urls.add(AbstractSecurityGlobalEnhanceFilter.DEFAULT_SECURITY_AUTHORIZATION_SERVER_METADATA_ENDPOINT_URI);

        urls.add(UriConstant.DEFAULT_LOGIN_URL + "**");
        Collection<String> suffixes = this.securityProperties.getResource().getPermitStaticResource() ?
                Arrays.asList(STATIC_RESOURCE) : Collections.EMPTY_LIST;

        return new PermitAllRequestMatcher(urls, suffixes);
    }

    @Override
    public RequestMatcher anonymous() {
        Set<String> urls = this.getUrls(this.securityProperties.getResource().getAnonymous());
        urls.addAll(Arrays.asList(UriConstant.ERROR_PAGE,
                // 权限拦截时默认的跳转地址
                securityProperties.getRedirectUrl(),
                // 登陆页面的URL
                securityProperties.getLoginPage()));

        List<RequestMatcher> matchers = urls.stream().filter(StringUtils::isNotBlank).map(AntPathRequestMatcher::new).collect(Collectors.toList());

        return new OrRequestMatcher(matchers);
    }

    @Override
    public RequestMatcher globalVerificationExclude() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        requestMatchers.add(this.permitAll());
        requestMatchers.add(this.anonymous());
        this.getUrls(this.securityProperties.getToken().getGlobalVerificationExcludeUrls()).stream().map(AntPathRequestMatcher::new).forEach(requestMatchers::add);
        return new OrRequestMatcher(requestMatchers);
    }


    @Override
    public boolean showDetail() {
        return BooleanUtils.isTrue(this.security().getShowDetail());
    }

    @Override
    public String contextPath() {
        return StringUtils.isBlank(this.contextPath) ? "" : this.contextPath.trim();
    }

    /**
     * 提取出Map里存储的URL
     *
     * @param list 存储资源路径的map
     * @return 所有过滤后的资源路径
     */
    private Set<String> getUrls(List<String> list) {
        if (null == list) {
            return new HashSet<>();
        }
        // @formatter:off
        Set<String> urls = list.stream().filter(StringUtils::isNotBlank).map(v -> Arrays.stream(v.split(","))
                        .collect(Collectors.toSet())).flatMap(Collection::stream)
                .filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toSet());
        // @formatter:on
        return urls;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
