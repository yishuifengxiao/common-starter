package com.yishuifengxiao.common.validation.repository;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.validation.entity.ValidateCode;

/**
 *  保存验证码与获取验证码的接口
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public interface CodeRepository {
	/**
	 * 验证码放入session时的前缀
	 */
	String SESSION_KEY_PREFIX = "SESSION_KEY_PREFIX_";
    /**
     * 保存验证码
     * @param request
     * @param key
     * @param code
     */
	void save(ServletWebRequest request, String key, ValidateCode code);
	/**
	 * 获取验证码
	 * @param request
	 * @param key
	 * @return
	 */
	ValidateCode get(ServletWebRequest request, String key);
	/**
	 * 移出验证码
	 * @param request
	 * @param key
	 */
	void remove(ServletWebRequest request, String key);
}
