/**
 *
 */
package com.yishuifengxiao.common.security.token.extractor;

import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.tool.encoder.Md5;
import com.yishuifengxiao.common.utils.HttpUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统信息提取器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityValueExtractor implements SecurityValueExtractor {

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    private PropertyResource propertyResource;

    @Override
    public String extractUsername(HttpServletRequest request, HttpServletResponse response) {
        String usernameParameter = propertyResource.security().getUsernameParameter();
        if (StringUtils.isBlank(usernameParameter)) {
            usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
        }
        return request.getParameter(usernameParameter.trim());
    }

    @Override
    public String extractPassword(HttpServletRequest request, HttpServletResponse response) {
        String passwordParameter = propertyResource.security().getPasswordParameter();
        if (StringUtils.isBlank(passwordParameter)) {
            passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
        }
        return request.getParameter(passwordParameter.trim());
    }

    /**
     * 从请求中提取出用户的唯一标识符,即用户设备id
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 用户的唯一标识符
     */
    @Override
    public String extractDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceIdParameter = propertyResource.security().getToken().getUserDeviceId();
        if (StringUtils.isBlank(deviceIdParameter)) {
            deviceIdParameter = TokenConstant.USER_DEVICE_ID;
        }
        String deviceIdValue = request.getHeader(deviceIdParameter);
        if (StringUtils.isBlank(deviceIdValue)) {
            deviceIdValue = request.getParameter(deviceIdParameter);
        }
        if (StringUtils.isBlank(deviceIdValue)) {
            if (BooleanUtils.isTrue(propertyResource.security().getToken().getUseUserAgent())) {
                // 使用sessionId作为用户的唯一标识符
                deviceIdValue = HttpUtils.userAgent(request);
                if (null != deviceIdValue) {
                    deviceIdValue = Md5.md5Short(deviceIdValue);
                }
            }
        }
        return deviceIdValue;
    }

    public SimpleSecurityValueExtractor(PropertyResource propertyResource) {
        this.propertyResource = propertyResource;
    }

}
