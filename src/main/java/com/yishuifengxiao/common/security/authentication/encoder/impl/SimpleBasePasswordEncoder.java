package com.yishuifengxiao.common.security.authentication.encoder.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.security.authentication.encoder.BasePasswordEncoder;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.tool.encoder.DES;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义加密类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleBasePasswordEncoder extends BasePasswordEncoder {

    /**
     * 是否显示日志
     */
    private boolean show = false;

    /**
     * 加解密时用到密钥
     */
    private String key;

    @Override
    public String encode(CharSequence rawPassword) {
        String encodedPassword = DES.encrypt(key, rawPassword.toString());
        if (show) {
            log.info("【加密】自定义加密类中需要加密的密码的明文为 {},加密后的密码为 {}", rawPassword, encodedPassword);
        }

        return encodedPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        boolean result = false;
        if (StringUtils.isNoneBlank(rawPassword, encodedPassword)
                && StringUtils.equals(this.encode(rawPassword), encodedPassword)) {
            result = true;
        }
        if (show) {
            log.info("【验证】自定义加密类中需要比较的两个密码分别为 ， 前端输入的原始密码= {},目标密码为 ={} ,比较结果为{}", rawPassword, encodedPassword,
                    result);
        }

        return result;
    }

    @Override
    public String decode(String encodedPassword) {
        return DES.decrypt(key, encodedPassword);
    }

    @Override
    public boolean supportDecode() {
        return true;
    }

    public SimpleBasePasswordEncoder() {

    }

    public SimpleBasePasswordEncoder(PropertyResource propertyResource) {
        this.show = propertyResource.showDetail();
        this.key = propertyResource.security().getSecretKey();
    }

}