/**
 * 
 */
package com.yishuifengxiao.common.security.processor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yishuifengxiao.common.tool.entity.Response;

/**
 * 协助处理器<br/>
 * 用于处理各种 系统handler的情况
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
public interface HandlerProcessor {

	/**
	 * 对系统中的各种handler进行协助处理
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @param isRedict 是否跳转
	 * @param url      希望跳转的路径
	 * @param result   传输过来的信息,如果是异常时就是异常，如果是正常时就是授权信息
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("rawtypes")
	public void handle(HttpServletRequest request, HttpServletResponse response, Boolean isRedict, String url,
			Response result) throws IOException, ServletException;

}
