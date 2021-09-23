package com.yishuifengxiao.common.code.repository;

import com.yishuifengxiao.common.code.entity.ValidateCode;

/**
 * 验证码存储器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CodeRepository {

	/**
	 * 存储验证码
	 * 
	 * @param key  验证码的唯一标识符
	 * @param code 需要存储的验证码
	 */
	void save(String key, ValidateCode code);

	/**
	 * 根据验证码的唯一标识符获取存储的验证码
	 * 
	 * @param key 验证码的唯一标识符
	 * @return 存储的验证码
	 */
	ValidateCode get(String key);

	/**
	 * 根据验证码的唯一标识符移除存储的验证码
	 * 
	 * @param key 验证码的唯一标识符
	 */
	void remove(String key);
}
