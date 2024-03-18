package com.yishuifengxiao.common.support;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 国际化组件
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface I18nHelper {


    /**
     * 获取国际化信息
     *
     * @param request HttpServletRequest
     * @param code    the message code to look up, e.g. 'calculator
     * @return the resolved message if the lookup was successful, otherwise the default message passed as a parameter (which may be null)
     */
    String getMessage(HttpServletRequest request, String code);

}
