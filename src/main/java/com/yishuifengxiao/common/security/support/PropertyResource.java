/**
 *
 */
package com.yishuifengxiao.common.security.support;

import com.yishuifengxiao.common.security.SecurityProperties;

import java.util.List;
import java.util.Set;

/**
 * <p>资源管理器</p>
 * 管理系统中所有的资源
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PropertyResource {

    /**
     * spring security 相关的配置
     *
     * @return spring security 相关的配置
     */
    SecurityProperties security();


    /**
     * 获取所有直接放行的资源
     *
     * @return 直接放行的资源
     */
    Set<String> allPermitUrs();

    /**
     * 获取所有允许匿名访问的资源
     *
     * @return 所有允许匿名访问的资源
     */
    List<String> anonymousUrls();


    /**
     * 获取所有不需要经过token校验的资源的路径
     *
     * @return 不需要经过token校验的资源的路径
     */
    Set<String> allUnCheckUrls();

    /**
     * 获取所有忽视的资源
     *
     * @return 忽视的资源
     */
    String[] allIgnoreUrls();

    /**
     * 是否显示加载细节
     *
     * @return true表示显示加载细节，false不显示
     */
    boolean showDetail();

}
