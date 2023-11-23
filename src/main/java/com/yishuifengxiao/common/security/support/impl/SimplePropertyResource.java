/**
 *
 */
package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.constant.UriConstant;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AbstractSecurityGlobalEnhanceFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
public class SimplePropertyResource implements PropertyResource {

    /**
     * 系统默认包含的静态路径
     */
    private static String[] STATIC_RESOURCE = new String[]{"/js/**", "/css/**", "/images/**", "/fonts/**", "/**/**" + ".png", "/**/**.jpg", "/**/**.html", "/**/**.ico", "/**/**.js", "/**/**.css", "/**/**.woff", "/**/**.ttf"};

    /**
     * 系统默认包含的swagger-ui资源路径
     */
    private static String[] SWAGGER_UI_RESOURCE = new String[]{"/swagger-ui.html", "/swagger-resources/**", "/v2/api" + "-docs", "/swagger-ui/**", "/v3/**"};
    /**
     * 系统默认包含actuator相关的路径
     */
    private static String[] ACTUATOR_RESOURCE = new String[]{"/actuator/**"};
    /**
     * 系统默认包含webjars相关的路径
     */
    private static String[] WEBJARS_RESOURCE = new String[]{"/webjars/**"};
    /**
     * 所有的资源
     */
    private static String[] ALL_RESOURCE = new String[]{"/**"};

    /**
     * spring security 属性配置文件
     */
    private SecurityProperties securityProperties;


    /**
     * 是否显示详细信息日志
     */
    private boolean show = false;

    private String contextPath;

    @Override
    public SecurityProperties security() {
        return this.securityProperties;
    }


    @Override
    public Set<String> allPermitUrs() {
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
        if (show) {
            log.info("【yishuifengxiao-common-spring-boot-starter】所有直接放行的资源的为 {}", StringUtils.join(urls, " ; "));
        }

        return urls;
    }

    @Override
    public List<String> anonymousUrls() {
        Set<String> urls = this.getUrls(this.securityProperties.getResource().getAnonymous());
        urls.addAll(Arrays.asList(UriConstant.ERROR_PAGE,
                // 权限拦截时默认的跳转地址
                securityProperties.getRedirectUrl(),
                // 登陆页面的URL
                securityProperties.getLoginPage()));

        return urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }


    @Override
    public Set<String> allUnCheckUrls() {
        Set<String> urls = new HashSet<>();
        // 所有直接放行的资源
        urls.addAll(this.allPermitUrs());
        // 所有允许匿名访问的资源
        urls.addAll(this.anonymousUrls());
        //表单登陆时form表单请求的地址
        urls.add(securityProperties.getFormActionUrl());
        // 所有忽视的资源
        urls.addAll(Arrays.asList(this.allIgnoreUrls()));
        return urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    @Override
    public String[] allIgnoreUrls() {
        Set<String> set = new HashSet<>();
        final SecurityProperties.IgnoreProperties ignore = this.securityProperties.getResource().getIgnore();
        if (ignore.getContainStaticResource()) {
            set.addAll(Arrays.asList(STATIC_RESOURCE));
        }
        if (ignore.getContainStaticResource()) {
            set.addAll(Arrays.asList(SWAGGER_UI_RESOURCE));
        }
        if (ignore.getContainActuator()) {
            set.addAll(Arrays.asList(ACTUATOR_RESOURCE));
        }
        if (ignore.getContainWebjars()) {
            set.addAll(Arrays.asList(WEBJARS_RESOURCE));
        }
        if (ignore.getContainAll()) {
            set.addAll(Arrays.asList(ALL_RESOURCE));
        }
        if (ignore.getContainErrorPage()) {
            // 错误页面
            set.add(UriConstant.ERROR_PAGE);
        }
        //security全局增强元数据
        set.add(AbstractSecurityGlobalEnhanceFilter.DEFAULT_SECURITY_AUTHORIZATION_SERVER_METADATA_ENDPOINT_URI);

        set.add(UriConstant.DEFAULT_LOGIN_URL + "**");
        set.addAll(this.getUrls(ignore.getUrls()));

        if (show) {
            log.info("【yishuifengxiao-common-spring-boot-starter】所有忽视管理的资源的为 {}", StringUtils.join(set, " ; "));
        }
        return set.toArray(new String[]{});
    }

    @Override
    public boolean showDetail() {
        return BooleanUtils.isTrue(this.security().getShowDetail());
    }

    @Override
    public String contextPath() {
        return StringUtils.isBlank(this.contextPath) ? "" : this.contextPath.trim();
    }

    @Override
    public Set<String> definedUrls() {
        // 所有已经明确了权限的路径
        Set<String> urls = new HashSet<>();
        urls.addAll(Arrays.stream(this.allIgnoreUrls()).collect(Collectors.toSet()));
        urls.addAll(this.allPermitUrs());
        urls.addAll(this.anonymousUrls());
        return urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
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
        Set<String> urls = list.parallelStream().filter(StringUtils::isNotBlank).map(v -> Arrays.stream(v.split(","))
                        .collect(Collectors.toSet())).flatMap(Collection::stream)
                .filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toSet());
        // @formatter:on
        return urls;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.show = BooleanUtils.isTrue(securityProperties.getShowDetail());
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
