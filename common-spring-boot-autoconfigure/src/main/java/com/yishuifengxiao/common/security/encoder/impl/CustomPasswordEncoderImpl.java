package com.yishuifengxiao.common.security.encoder.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yishuifengxiao.common.security.encoder.CustomPasswordEncoder;
import com.yishuifengxiao.common.tool.encoder.DES;

/**
 * 自定义加密类
 * 
 * @author admin
 *
 */
public class CustomPasswordEncoderImpl extends CustomPasswordEncoder {

	private final static Logger log = LoggerFactory.getLogger(CustomPasswordEncoderImpl.class);
	/**
	 * 加解密时用到密钥
	 */
	private String key;

	@Override
	public String encode(CharSequence rawPassword) {
		String encodedPassword = DES.encrypt(key, rawPassword.toString());
		log.debug("【加密】自定义加密类中需要加密的密码的明文为 {},加密后的密码为 {}", rawPassword, encodedPassword);
		return encodedPassword;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		boolean result = false;
		if (StringUtils.isNoneBlank(rawPassword, encodedPassword)
				&& StringUtils.equals(this.encode(rawPassword), encodedPassword)) {
			result = true;
		}
		log.debug("【验证】自定义加密类中需要比较的两个密码分别为 ， 前端输入的原始密码= {},目标密码为 ={} ,比较结果为{}", rawPassword, encodedPassword,
				result);
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public CustomPasswordEncoderImpl(String key) {
		this.key = key;
	}

	public CustomPasswordEncoderImpl() {

	}

}