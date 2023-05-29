package com.yishuifengxiao.common.security.support;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全功能全局增强
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SecurityGlobalEnhance extends OncePerRequestFilter implements WebMvcConfigurer {
    /**
     * The default endpoint {@code URI} for security Server Metadata requests.
     */
    public static final String DEFAULT_SECURITY_AUTHORIZATION_SERVER_METADATA_ENDPOINT_URI =
            "/.well-known/security/meta";

    /**
     * 默认的security曾倩ui前缀
     */
    public static final String DEFAULT_SECURITY_UI_PREFIX = "/security-enhance-ui/";
}
