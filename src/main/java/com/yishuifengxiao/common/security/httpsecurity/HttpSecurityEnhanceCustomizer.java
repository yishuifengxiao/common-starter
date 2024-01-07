/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity;

import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.SecurityPropertyResource;

/**
 * <p>
 * 授权提供器
 * </p>
 * <p>
 * 对系统进行授权配置操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HttpSecurityEnhanceCustomizer {

    /**
     * 授权配置
     *
     * @param securityPropertyResource    授权资源
     * @param authenticationPoint 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据
     * @param http                HttpSecurity
     * @throws Exception 配置时出现问题
     */
    void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception;

    /**
     * 授权提供器的顺序，数字越小越是提前使用，默认值为100
     *
     * @return 授权提供器的顺序
     */
    default int order() {
        return 100;
    }


}
