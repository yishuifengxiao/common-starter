package com.yishuifengxiao.common.security.token.extractor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 信息提取器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityValueExtractor {

    /**
     * 从请求中提取出用户的登陆账号
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 用户的登陆账号
     */
    String extractUsername(HttpServletRequest request, HttpServletResponse response);

    /**
     * 从请求中提取出用户的登陆密码
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 用户的登陆密码
     */
    String extractPassword(HttpServletRequest request, HttpServletResponse response);

    /**
     * 从请求中提取出用户的唯一标识符,即用户设备id
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 用户的唯一标识符
     */
    String extractDeviceId(HttpServletRequest request, HttpServletResponse response);

}
