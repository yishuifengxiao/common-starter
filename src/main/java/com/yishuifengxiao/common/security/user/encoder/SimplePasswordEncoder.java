package com.yishuifengxiao.common.security.user.encoder;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.tool.codec.DES;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义加密类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimplePasswordEncoder implements PasswordEncoder {

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
        if (StringUtils.isNoneBlank(rawPassword, encodedPassword) && StringUtils.equals(this.encode(rawPassword),
                encodedPassword)) {
            result = true;
        }
        if (show) {
            log.info("【验证】自定义加密类中需要比较的两个密码分别为 ， 前端输入的原始密码= {},目标密码为 ={} ,比较结果为{}", rawPassword, encodedPassword,
                    result);
        }

        return result;
    }


    public SimplePasswordEncoder() {

    }

    public SimplePasswordEncoder(SecurityPropertyResource securityPropertyResource) {
        this.show = securityPropertyResource.showDetail();
        this.key = securityPropertyResource.security().getSecretKey();
    }

}