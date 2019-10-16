package com.yishuifengxiao.common.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.tool.entity.Response;

/**
 * 异常提示信息转换类转换成异常信息
 * 
 * @author yishui
 * @date 2019年7月25日
 * @version 1.0.0
 */
public final class ExceptionUtil {

	/**
	 * 根据异常信息提取出对应的异常信息
	 * 
	 * @param e 造成异常的原因
	 * @return 响应
	 */
	public final static Response<String> extract(Map<String, String> map, Exception e) {

		String causeName = e.getCause() != null ? e.getCause().getClass().getSimpleName() : "";
		String errMsg = getMsg(map, causeName);
		return Response.error(errMsg);

	}

	/**
	 * 根据异常类型获取到提示信息
	 * 
	 * @param map
	 * @param causeName 造成异常的原因的类的名字
	 * @return
	 */
	private static String getMsg(Map<String, String> map, String causeName) {
		String msg = "请求失败";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (StringUtils.containsIgnoreCase(entry.getKey(), causeName)) {
				msg = entry.getValue();
				break;
			}
		}
		return msg;

	}

}
