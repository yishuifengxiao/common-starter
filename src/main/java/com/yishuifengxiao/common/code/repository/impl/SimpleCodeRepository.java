/**
 * 
 */
package com.yishuifengxiao.common.code.repository.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.repository.CodeRepository;

/**
 * 基于内存实现的验证码存储器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleCodeRepository implements CodeRepository {

	private final static Map<String, ValidateCode> MAP = new ConcurrentHashMap<>();

	/**
	 * 存储验证码
	 * 
	 * @param key  验证码的唯一标识符
	 * @param code 需要存储的验证码
	 */
	@Override
	public synchronized void save(String key, ValidateCode code) {
		MAP.put(key, code);

	}

	/**
	 * 根据验证码的唯一标识符获取存储的验证码
	 * 
	 * @param key 验证码的唯一标识符
	 * @return 存储的验证码
	 */
	@Override
	public synchronized ValidateCode get(String key) {
		ValidateCode code = MAP.get(key);
		return code;
	}

	/**
	 * 根据验证码的唯一标识符移除存储的验证码
	 * 
	 * @param key 验证码的唯一标识符
	 */
	@Override
	public synchronized void remove(String key) {
		Iterator<String> it = MAP.keySet().iterator();
		synchronized (SimpleCodeRepository.class) {
			while (it.hasNext()) {
				String currentKey = it.next();
				MAP.keySet().remove(currentKey);
			}

		}

	}

}
