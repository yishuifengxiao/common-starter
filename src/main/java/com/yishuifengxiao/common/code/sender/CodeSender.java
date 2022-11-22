package com.yishuifengxiao.common.code.sender;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码发送器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CodeSender {

    /**
     * <p>
     * 发送验证码到指定的目标
     * </p>
     * 一般来说发送目标的含义如下:
     *
     * <ul>
     * <li>对于短信验证码，一般来说标识符为发送目标的手机号</li>
     * <li>对于邮件验证码，一般来说标识符为发送目标的邮箱地址</li>
     * <li>对于图形验证码，一般来说为与用户约定的字符</li>
     * </ul>
     *
     * @param <T>     验证码的类型
     * @param request 用户请求
     * @param target  验证码发送目标
     * @param code    验证码的内容
     * @throws CustomException 发送时出现问题
     */
    <T extends ValidateCode> void send(ServletWebRequest request, String target, T code) throws CustomException;
}