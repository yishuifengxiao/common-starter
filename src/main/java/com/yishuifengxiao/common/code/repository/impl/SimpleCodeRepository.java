/**
 * 
 */
package com.yishuifengxiao.common.code.repository.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.repository.CodeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 验证码内存管理器<br/>
 * 验证码保存在内存中
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
@Slf4j
public class SimpleCodeRepository implements CodeRepository {


	private final static Map<String, ValidateCode> MAP = new ConcurrentHashMap<>();

	@Override
	public void save(ServletWebRequest request, String key, ValidateCode code) {
		log.debug("验证码存取的默认实现类 保存的键为 {},值为 {}", key, code);
		MAP.put(key, code);

	}

	@Override
	public ValidateCode get(ServletWebRequest request, String key) {
		ValidateCode code = MAP.get(key);
		log.debug("验证码存取的默认实现类 获取的键为 {},值为 ", key);
		return code;
	}

	@Override
	public void remove(ServletWebRequest request, String key) {
		Iterator<String> it = MAP.keySet().iterator();
		synchronized (SimpleCodeRepository.class) {
			while (it.hasNext()) {
				String currentKey = it.next();
				MAP.keySet().remove(currentKey);
			}

		}

	}

}
