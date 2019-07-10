/**
 * 
 */
package com.yishuifengxiao.common.validation.holder;

import java.util.Map;

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
			throw new ValidateException("验证码处理器" + codeProcessorName + "不存在");
		}
		return processor;
	}

	public CodeProcessorHolder(Map<String, CodeProcessor> codeProcessors) {
		this.codeProcessors = codeProcessors;
	}

	public CodeProcessorHolder() {

	}

}
