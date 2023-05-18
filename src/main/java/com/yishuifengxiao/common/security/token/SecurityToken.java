/**
 *
 */
package com.yishuifengxiao.common.security.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.tool.encoder.Md5;
import com.yishuifengxiao.common.tool.random.IdWorker;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

/**
 * 自定义访问令牌
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */

@ApiModel(value = "自定义访问令牌")
public class SecurityToken extends AbstractAuthenticationToken implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4651167209282446068L;

    /**
     * <p>token的值，不区分大小写</p>
     * <p>一般值的内容为 username:deviceId:issueAt的DES加密值</p>
     */
    @ApiModelProperty("token的值")
    private final String value;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private Object principal;

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String deviceId;

    /**
     * token的有效时间，单位为秒
     */
    @ApiModelProperty("token的有效时间，单位为秒")
    private Integer validSeconds;

    /**
     * token的首次生成时间
     */
    @ApiModelProperty("token的首次生成时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime issueAt;

    /**
     * token的过期时间点
     */
    @ApiModelProperty("token的过期时间点")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expireAt;

    /**
     * <p>当前token是否处于有效状态</p>
     * <p>true表示有效，false无效</p>
     * 例如在token数量有限时，驱逐策略下，早期的token可能就处于失效状态
     */
    @ApiModelProperty("当前token是否处于有效状态")
    private Boolean isActive;


    /**
     * 当前token是否已经过期
     *
     * @return true表示已过期，false表示未过期
     */
    public boolean isExpired() {
        if (null != this.expireAt) {
            return this.expireAt.isBefore(LocalDateTime.now());
        }
        return false;

    }

    /**
     * <p>当前token是否是可用状态</p>
     * 当token的有效状态不为true或已经过期时当前token不是可用状态
     *
     * @return true表示可用状态，false表示不可用状态
     */
    @JsonIgnore
    public boolean isAvailable() {
        if (BooleanUtils.isFalse(this.isActive)) {
            return false;
        }
        if (this.isExpired()) {
            return false;
        }
        return true;
    }

    /**
     * 重置token的过期时间
     *
     * @return 当前对象
     */
    public SecurityToken refreshExpireTime() {
        // 重新设置token的过期时间
        this.setExpireAt(LocalDateTime.now().plusSeconds(this.validSeconds.longValue()));
        return this;

    }

    /**
     * 获取当前token的值
     *
     * @return 当前token的值
     */
    public String getValue() {
        return value;
    }


    /**
     * 获取设备id
     *
     * @return 设备id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备id
     *
     * @param deviceId 设备id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取token的有效时间
     *
     * @return 单位为秒
     */
    public Integer getValidSeconds() {
        if (null == this.validSeconds || this.validSeconds <= 0) {
            return TokenConstant.TOKEN_VALID_TIME_IN_SECOND;
        }
        return validSeconds;
    }

    /**
     * 设置token的有效时间
     *
     * @param validSeconds 单位为秒
     */
    public void setValidSeconds(Integer validSeconds) {
        if (null == validSeconds || validSeconds <= 0) {
            this.validSeconds = TokenConstant.TOKEN_VALID_TIME_IN_SECOND;
        }
        this.validSeconds = validSeconds;
    }


    /**
     * 获取token的首次生成时间
     *
     * @return token的首次生成时间
     */
    public LocalDateTime getIssueAt() {
        return issueAt;
    }

    /**
     * 设置token的首次生成时间
     *
     * @param issueAt token的首次生成时间
     */
    public void setIssueAt(LocalDateTime issueAt) {
        this.issueAt = issueAt;
    }

    /**
     * 获取token的过期时间点
     *
     * @return token的过期时间点
     */
    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    /**
     * 设置 token的过期时间点
     *
     * @param expireAt token的过期时间点
     */
    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    /**
     * 获取当前token是否为有效状态
     *
     * @return 当前token是否为有效状态，true表示有效。false表示无效
     */
    @JsonIgnore
    public Boolean isActive() {
        return isActive;
    }

    /**
     * 设置token是否为有效状态
     *
     * @param isActive token是否为有效状态，true表示有效。false表示无效
     */
    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @param principal    用户名
     * @param deviceId     设备id
     * @param validSeconds token有效时间
     * @param authorities  authorities the collection of <tt>GrantedAuthority</tt>s for the principal represented by
     *                     this authentication object.
     */
    public SecurityToken(String principal, String deviceId, Integer validSeconds, Collection<?
            extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        if (null == validSeconds || validSeconds <= 0) {
            validSeconds = TokenConstant.TOKEN_VALID_TIME_IN_SECOND;
        }
        this.principal = principal;
        this.deviceId = deviceId;
        this.validSeconds = validSeconds;
        this.issueAt = LocalDateTime.now();
        this.expireAt = this.issueAt.plusSeconds(validSeconds.longValue());
        this.isActive = true;
        this.value =
                Md5.md5Short(new StringBuilder(principal).append(deviceId).append(IdWorker.snowflakeId()).toString());
    }


    public SecurityToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.value = null;
    }

    private SecurityToken() {
        super(null);
        this.value = null;
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityToken)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SecurityToken that = (SecurityToken) o;
        return value.equals(that.value) && principal.equals(that.principal) && deviceId.equals(that.deviceId) && validSeconds.equals(that.validSeconds) && issueAt.equals(that.issueAt) && expireAt.equals(that.expireAt) && isActive.equals(that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, principal, deviceId, validSeconds, issueAt, expireAt, isActive);
    }

    @Override
    public String toString() {
        return "SecurityToken{" +
                "value='" + value + '\'' +
                ", principal=" + principal +
                ", deviceId='" + deviceId + '\'' +
                ", validSeconds=" + validSeconds +
                ", issueAt=" + issueAt +
                ", expireAt=" + expireAt +
                ", isActive=" + isActive +
                '}';
    }
}
