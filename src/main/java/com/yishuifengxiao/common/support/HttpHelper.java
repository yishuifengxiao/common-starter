package com.yishuifengxiao.common.support;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * http请求增强器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpHelper {
    /**
     * json请求标志
     */
    private final static String JSON_FLAG = "json";

    /**
     * 是否为json请求
     *
     * @param request
     * @return
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (StringUtils.containsIgnoreCase(contentType, JSON_FLAG)) {
            return true;
        }
        String accept = null;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement();
            if (StringUtils.equalsIgnoreCase(nextElement, "Accept")) {
                accept = nextElement;
                break;
            }
        }
        if (StringUtils.isBlank(accept)) {
            return false;
        }
        final String acceptVal = request.getHeader(accept);
        if (StringUtils.containsIgnoreCase(acceptVal, JSON_FLAG)) {
            return true;
        }
        return false;
    }
}
