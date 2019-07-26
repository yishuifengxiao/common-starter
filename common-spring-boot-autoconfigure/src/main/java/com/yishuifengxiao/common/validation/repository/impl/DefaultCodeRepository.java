/**
 * 
 */
package com.yishuifengxiao.common.validation.repository.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.validation.entity.ValidateCode;
import com.yishuifengxiao.common.validation.repository.CodeRepository;

/**
 * 验证码内存管理器<br/>
 * 验证码保存在内存中
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class DefaultCodeRepository implements CodeRepository {
	private final static Logger log = LoggerFactory.getLogger(DefaultCodeRepository.class);

	private final static Map<String, ValidateCode> map = new ConcurrentHashMap<>();

	@Override
	public void save(ServletWebRequest request, String key, ValidateCode code) {
		log.debug("验证码存取的默认实现类 保存的键为 {},值为 {}", key, code);
		map.put(key, code);

	}

	@Override
	public ValidateCode get(ServletWebRequest request, String key) {
		ValidateCode code = map.get(key);
		log.debug("验证码存取的默认实现类 获取的键为 {},值为 ", key);
		return code;
	}

	@Override
	public void remove(ServletWebRequest request, String key) {
		Iterator<String> it = map.keySet().iterator();
		synchronized (DefaultCodeRepository.class) {
			while (it.hasNext()) {
				String currentKey = it.next();
				map.keySet().remove(currentKey);
			}

		}

	}

}
