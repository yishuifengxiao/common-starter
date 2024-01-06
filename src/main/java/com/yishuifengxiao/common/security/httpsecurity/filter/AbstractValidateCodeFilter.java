package com.yishuifengxiao.common.security.httpsecurity.filter;

import com.yishuifengxiao.common.code.CodeProducer;
import com.yishuifengxiao.common.code.eunm.CodeType;
import com.yishuifengxiao.common.security.httpsecurity.AbstractSecurityRequestFilter;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.tool.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 验证码过滤器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class AbstractValidateCodeFilter extends AbstractSecurityRequestFilter implements InitializingBean {
    /**
     * 用于定义路由规则，因为下面的路径里有统配符，验证请求的URL与配置的URL是否匹配的类
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 存放所有需要校验验证码的url【即表明什么样的URL需要用到什么样的验证码】
     */
    private final Map<String, CodeType> urlMap = new HashMap<>();


    /**
     * 协助处理器
     */
    private SecurityHandler securityHandler;

    /**
     * 验证码处理器
     */
    private CodeProducer codeProducer;

    private PropertyResource propertyResource;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

        // 需要拦截的路径
        propertyResource.security().getCode().getFilter().forEach((codeType, urls) -> {
            addUrlTpMap(urls, CodeType.parse(codeType));
        });
    }

    /**
     * 将系统需要校验验证码的URL根据校验的类型放入map中
     *
     * @param urlString        需要校验验证码的URL
     * @param validateCodeType 验证码类型
     */
    protected void addUrlTpMap(String urlString, CodeType validateCodeType) {
        if (StringUtils.isNotBlank(urlString) && validateCodeType != null) {
            String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString, ",");
            for (String url : urls) {
                urlMap.put(url, validateCodeType);
            }
        }

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CodeType validateCodeType = getValidateCodeType(request);
        if (validateCodeType != null) {
            if (propertyResource.showDetail()) {
                log.info("【验证码过滤器】 获取校验码类型时的URL为 {}，请求类型为 {}", request.getRequestURI(), request.getMethod());
            }

            try {

                if (propertyResource.showDetail()) {
                    log.info("【验证码过滤器】  请求校验{}中的验证码的的类型是 {} ,校验器类型为 {}", request.getRequestURI(), validateCodeType,
                            codeProducer);
                }

                codeProducer.validate(new ServletWebRequest(request, response), validateCodeType);
            } catch (CustomException exception) {
                if (propertyResource.showDetail()) {
                    log.info("验证码验证校验未通过，出现问题 {}", exception.getMessage());
                }
                securityHandler.onException(propertyResource, request, response, exception);

                // 失败后不执行后面的过滤器
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 获取校验码的类型，如果当前请求不需要校验，则返回null
     *
     * @param request
     * @return
     */
    private CodeType getValidateCodeType(HttpServletRequest request) {
        CodeType result = null;

        if (!propertyResource.security().getCode().getIsFilterGet()
                && StringUtils.equalsIgnoreCase(request.getMethod(), "get")) {
            return null;
        }
        // 根据请求url获取拦截器类型
        Set<String> urls = urlMap.keySet();
        for (String url : urls) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                result = urlMap.get(url);
                break;
            }
        }

        return result;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(this, AbstractPreAuthenticatedProcessingFilter.class);

    }

    public PropertyResource getPropertyResource() {
        return propertyResource;
    }

    public void setPropertyResource(PropertyResource propertyResource) {
        this.propertyResource = propertyResource;
    }

    public CodeProducer getCodeProducer() {
        return codeProducer;
    }

    public void setCodeProducer(CodeProducer codeProducer) {
        this.codeProducer = codeProducer;
    }

    public SecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    public void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }
}