/**
 *
 */
package com.yishuifengxiao.common.code.entity;


import com.yishuifengxiao.common.tool.bean.JsonUtil;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 验证码基类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidateCode implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 251907201542125693L;

    /**
     * 验证码的失效时间
     */
    private LocalDateTime expireTime;

    /**
     * 验证码内容
     */
    private String code;

    /**
     * 构造函数
     *
     * @param expireTimeInSeconds 验证码的有效时间，单位 秒
     * @param code                验证码内容
     */
    public ValidateCode(long expireTimeInSeconds, String code) {
        this.expireTime = LocalDateTime.now().plusSeconds(expireTimeInSeconds);
        this.code = code;
    }

    /**
     * 获取验证码是否已经过期
     *
     * @return true表示已过期，false未过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expireTime);
    }

    /**
     * 获取验证码的失效时间
     *
     * @return 验证码的失效时间
     */
    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    /**
     * 设置验证码的失效时间
     *
     * @param expireTime 验证码的失效时间
     */
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 获取验证码的内容
     *
     * @return 验证码的内容
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置验证码的内容
     *
     * @param code 验证码的内容
     */
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {

        return JsonUtil.toJSONString(this);
    }

    /**
     * 构造函数
     */
    public ValidateCode() {

    }

}
