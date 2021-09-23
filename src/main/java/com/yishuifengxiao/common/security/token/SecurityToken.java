/**
 * 
 */
package com.yishuifengxiao.common.security.token;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang3.BooleanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yishuifengxiao.common.security.constant.TokenConstant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 自定义访问令牌
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@ApiModel(value = "自定义访问令牌")
public class SecurityToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4651167209282446068L;

	/**
	 * <p>token的值，不区分大小写</p>
	 * 一般值的内容为 username:clientId:currentTimeMillis
	 */
    @ApiModelProperty("token的值")
	private String value;

	/**
	 * 用户名
	 */
    @ApiModelProperty("用户名")
	private String username;

	/**
	 * 会话id
	 */
    @ApiModelProperty("会话id")
	private String sessionId;

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
	private LocalDateTime time;

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
	 * 设置token的值
	 * 
	 * @param value token的值
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 获取用户名
	 * 
	 * @return 用户名
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户名
	 * 
	 * @param username 用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取会话id
	 * 
	 * @return 会话id
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * 设置会话id
	 * 
	 * @param sessionId 会话id
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
	 * 获取token的生成时间
	 * 
	 * @return 生成时间
	 */
	public LocalDateTime getTime() {
		return time;
	}

	/**
	 * 设置token的生成时间
	 * 
	 * @param time token的生成时间
	 */
	public void setTime(LocalDateTime time) {
		this.time = time;
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
	 * 
	 * @param value        当前token的值
	 * @param username     用户名
	 * @param sessionId    会话id
	 * @param validSeconds token有效时间
	 */
	public SecurityToken(String value, String username, String sessionId, Integer validSeconds) {
		if (null == validSeconds || validSeconds <= 0) {
			validSeconds = TokenConstant.TOKEN_VALID_TIME_IN_SECOND;
		}
		this.value = value;
		this.username = username;
		this.sessionId = sessionId;
		this.validSeconds = validSeconds;
		this.time = LocalDateTime.now();
		this.expireAt = this.time.plusSeconds(validSeconds.longValue());
		this.isActive = true;

	}

	/**
	 * 
	 * @param value        当前token的值
	 * @param validSeconds token有效时间
	 */
	public SecurityToken(String value, Integer validSeconds) {
		this(value, null, null, validSeconds);
	}

	/**
	 * 
	 * @param value 当前token的值
	 */
	public SecurityToken(String value) {
		this(value, null, null, null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expireAt == null) ? 0 : expireAt.hashCode());
		result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((validSeconds == null) ? 0 : validSeconds.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SecurityToken other = (SecurityToken) obj;
		if (expireAt == null) {
			if (other.expireAt != null) {
				return false;
			}
		} else if (!expireAt.equals(other.expireAt)) {
			return false;
		}
		if (isActive == null) {
			if (other.isActive != null) {
				return false;
			}
		} else if (!isActive.equals(other.isActive)) {
			return false;
		}
		if (sessionId == null) {
			if (other.sessionId != null) {
				return false;
			}
		} else if (!sessionId.equals(other.sessionId)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		if (validSeconds == null) {
			if (other.validSeconds != null) {
				return false;
			}
		} else if (!validSeconds.equals(other.validSeconds)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SecurityToken [value=");
		builder.append(value);
		builder.append(", username=");
		builder.append(username);
		builder.append(", sessionId=");
		builder.append(sessionId);
		builder.append(", validSeconds=");
		builder.append(validSeconds);
		builder.append(", expireAt=");
		builder.append(expireAt);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append("]");
		return builder.toString();
	}

}
