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
import com.yishuifengxiao.common.tool.codec.MD5;
import com.yishuifengxiao.common.tool.random.IdWorker;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

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

@Schema(name = "自定义访问令牌")
public class SecurityToken extends AbstractAuthenticationToken implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4651167209282446068L;

    /**
     * 默认的匿名token
     */
    public final static SecurityToken anonymous = new SecurityToken("anonymous", "anonymous",
            null, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    /**
     * <p>token的值，不区分大小写</p>
     * <p>一般值的内容为 username:deviceId:issueAt的DES加密值</p>
     */
    @Schema(name = "token的值")
    private final String value;

    /**
     * 用户名
     */
    @Schema(name = "用户名")
    private Object principal;

    /**
     * 设备id
     */
    @Schema(name = "设备id")
    private String deviceId;

    /**
     * token的首次生成时间
     */
    @Schema(name = "token的首次生成时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime issueAt;

    /**
     * token的过期时间点
     */
    @Schema(name = "token的过期时间点")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expireAt;


    @JsonIgnore
    private transient UserDetails userDetails;


    /**
     * 重置token的过期时间
     *
     * @return 当前对象
     */
    public SecurityToken refreshExpireTime(long validSeconds) {
        // 重新设置token的过期时间
        this.setExpireAt(LocalDateTime.now().plusSeconds(validSeconds));
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
        return null != this.expireAt && !LocalDateTime.now().isAfter(this.expireAt);
    }


    /**
     * @param principal    用户名
     * @param deviceId     设备id
     * @param validSeconds token有效时间
     * @param authorities  authorities the collection of <tt>GrantedAuthority</tt>s for the
     *                     principal represented by
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
        this.issueAt = LocalDateTime.now();
        this.expireAt = this.issueAt.plusSeconds(validSeconds.longValue());
        this.value =
                MD5.md5Short(new StringBuilder(principal).append(deviceId).append(IdWorker.snowflakeId()).toString());
    }


    public SecurityToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.value = null;
    }

    private SecurityToken() {
        super(null);
        this.value = null;
    }


    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecurityToken that = (SecurityToken) o;
        return Objects.equals(value, that.value) && Objects.equals(principal, that.principal) && Objects.equals(deviceId, that.deviceId) && Objects.equals(issueAt, that.issueAt) && Objects.equals(expireAt, that.expireAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, principal, deviceId, issueAt, expireAt);
    }

    @Override
    public String toString() {
        return "SecurityToken{" + "value='" + value + '\'' + ", principal=" + principal + ", "
                + "deviceId='" + deviceId + '\'' + ", issueAt=" + issueAt + ", expireAt=" + expireAt + '}';
    }
}
