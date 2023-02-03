/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * <p>
 * 授权提供器
 * </p>
 *
 * 对系统进行授权配置操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AuthorizeProvider {

    /**
     * 授权配置
     *
     * @param propertyResource               授权资源
     * @param http HttpSecurity
     * @throws Exception 配置时出现问题
     */
    void apply(PropertyResource propertyResource, HttpSecurity http) throws Exception;

    /**
     * 授权提供器的顺序，数字越小越是提前使用，默认值为100
     *
     * @return 授权提供器的顺序
     */
    default int order() {
        return 100;
    }


}
