/**
 * 
 */
package com.yishuifengxiao.common.web.error;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.web.WebExceptionProperties;

/**
 * 异常信息补充提取工具
 * 
 * @author qingteng
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ErrorHandler {

	/**
	 * 根据异常原因生成对应的响应
	 * 
	 * @param exceptionProperties 全局异常捕获属性配置
	 * @param e                   异常信息
	 * @return 响应数据
	 */
	Response<Object> extractErrorMsg(WebExceptionProperties exceptionProperties, Throwable e);

}
