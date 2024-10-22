package com.yishuifengxiao.common.cache;

import com.yishuifengxiao.common.tool.codec.Md5;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;


/**
 * Cache key generator. Used for creating a key based on the given method(used
 * as context) and its parameters.
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */

/**
 * <p>
 * 缓存key生成器
 * </p>
 * key的生成策略如下: 先依次获取下列信息
 * <ul>
 * <li>当前请求的实例的类的简称</li>
 * <li>当前请求的方法的名字</li>
 * <li>当前请求的所有的请求参数</li>
 * </ul>
 * 然后得到一个 按照 当前请求的实例的类的简称:当前请求的方法的名字:非空参数的toString()拼接的字符串,最后获取该字符串的16位的MD5的值
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleKeyGenerator implements KeyGenerator {

	/**
	 * Generate a key for the given method and its parameters.
	 * 
	 * @param target the target instance
	 * @param method the method being called
	 * @param params the method parameters (with any var-args expanded)
	 * @return a generated key
	 */
	@Override
	public Object generate(Object target, Method method, Object... params) {

		StringBuilder prefix = new StringBuilder(target.getClass().getSimpleName()).append(":").append(method.getName())
				.append(":");

		StringBuilder values = new StringBuilder("");
		if (null != params) {
			for (Object param : params) {
				if (null != param) {
					values.append(param.toString());
				}
			}
		}
		return prefix.append(Md5.md5Short(values.toString())).toString();

	}

}
