package com.yishuifengxiao.common.security.websecurity;

import java.util.List;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.websecurity.provider.WebSecurityProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleWebSecurityManager implements WebSecurityManager {

    private List<WebSecurityProvider> webSecurityProviders;

    private PropertyResource propertyResource;


    @Override
    public void config(WebSecurity web) throws Exception {

        if (null != this.webSecurityProviders) {
            for (WebSecurityProvider webSecurityProvider : webSecurityProviders) {
                if (propertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( web安全授权器 ) 实例为 {}", webSecurityProvider);
                }
                webSecurityProvider.configure(propertyResource, web);
            }
        }
    }

    public SimpleWebSecurityManager(List<WebSecurityProvider> webSecurityProviders, PropertyResource propertyResource) {
        this.webSecurityProviders = webSecurityProviders;
        this.propertyResource = propertyResource;
    }
}
