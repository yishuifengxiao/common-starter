package com.yishuifengxiao.common.web.error;

import com.yishuifengxiao.common.tool.entity.Response;

/**
 * 异常信息提取工具
 *
 * @author qingteng
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ExceptionHelper {

	/**
	 * 根据异常生产对应的响应信息
	 * 
	 * @param e          异常情况
	 * @param defaultMsg 缺省的提示信息
	 * @return 响应信息
	 */
	Response<Object> extract(Throwable e, String defaultMsg);

}
