/**
 * 
 */
package com.yishuifengxiao.common.validation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.eunm.CodeType;
import com.yishuifengxiao.common.validation.processor.CodeProcessor;

/**
 * 验证码持有器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class CodeProcessorHolder {

	private final static Logger log = LoggerFactory.getLogger(CodeProcessorHolder.class);

	private Map<String, CodeProcessor> codeProcessors;

	/**
	 * 根据验证码的类型获取到验证码处理
	 * 
	 * @param type
	 * @return
	 * @throws ValidateException
	 */
	public CodeProcessor findValidateCodeProcessor(CodeType type) throws ValidateException {
		return findValidateCodeProcessor(type.toString().toLowerCase());
	}

	/**
	 * 根据验证码的类型获取到验证码处理
	 * 
	 * @param type
	 * @return
	 * @throws ValidateException
	 */
	private CodeProcessor findValidateCodeProcessor(String type) throws ValidateException {
		String codeProcessorName = type + "CodeProcessor";
		CodeProcessor processor = codeProcessors.get(codeProcessorName);
		if (processor == null) {
			log.info("验证码处理器{}不存在", codeProcessorName);
			throw new ValidateException("验证码处理器不存在");
		}
		return processor;
	}

	public CodeProcessorHolder(Map<String, CodeProcessor> codeProcessors) {
		this.codeProcessors = codeProcessors;
	}

	public CodeProcessorHolder() {

	}

}
