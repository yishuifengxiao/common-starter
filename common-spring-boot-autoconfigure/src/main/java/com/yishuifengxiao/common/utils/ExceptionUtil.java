package com.yishuifengxiao.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.random.UID;

/**
 * 异常提示信息转换类转换成异常信息
 * 
 * @author yishui
 * @date 2019年7月25日
 * @version 1.0.0
 */
public final class ExceptionUtil {

	/**
	 * 异常信息提示 键: 异常类的简写名字 值: 异常提示信息
	 */
	private final static Map<String, Response<String>> map = new HashMap<>();

	static {
		map.put("ConstraintViolationException",
				new Response<String>(Response.Const.CODE_BAD_REQUEST, "已经存在相似的数据,不能重复添加"));
		map.put("DataIntegrityViolationException",
				new Response<String>(Response.Const.CODE_BAD_REQUEST, "已经存在相似的数据,不能重复添加"));
	}

	/**
	 * 根据异常信息提取出对应的异常信息
	 * 
	 * @param e 造成异常的原因
	 * @return 响应
	 */
	public final static Response<String> extract(Exception e) {

		String causeName = e.getCause() != null ? e.getCause().getClass().getSimpleName() : "";
		Response<String> response = map
				.getOrDefault(causeName, new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败"))
				.setId(UID.uuid());
		return response;

	}

}
