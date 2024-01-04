/**
 * 
 */
package com.yishuifengxiao.common.code.generator;



import com.yishuifengxiao.common.guava.GuavaCache;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;



import lombok.extern.slf4j.Slf4j;

/**
 * 抽象验证码生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public abstract class BaseCodeGenerator implements CodeGenerator {

	/**
	 * <p>
	 * 根据可以从请求中提取内容
	 * </p>
	 * 该找接口主要的作用是根据 标识符key从请求中提取出内容，提取策略如下:
	 * <ul>
	 * <li>先从请求头中提取名为标识符key内容</li>
	 * <li>接着从请求查询参数中获取名字为 标识符key的内容</li>
	 * <li>然后从请求上下文获取名字为 标识符key的内容</li>
	 * <li>其次从Session获取名字为 标识符key的内容</li>
	 * <li>最后从cookie获取名字为 标识符key的内容</li>
	 * </ul>
	 * 
	 * @param request 用户请求
	 * @param key     验证码标识符
	 * @return 提取出来的内容
	 */
	protected String extract(HttpServletRequest request, String key) {

		// 先从请求头中获取
		String value = request.getHeader(key);

		if (StringUtils.isBlank(value)) {
			// 如果没有获取到就从请求参数里获取
			value = request.getParameter(key);
		}

		try {
			if (StringUtils.isBlank(value)) {
				// 如果还是没有，就从请求上下文获取
				key = (String) GuavaCache.get(key);
			}

			if (StringUtils.isBlank(value)) {
				// 如果还是没有，就从session中获取
				value = (String) request.getSession().getAttribute(key);
			}
		} catch (Exception e) {
			log.warn("【yishuifengxiao-common-spring-boot-starter】验证码信息提取器中提取信息{}对应的值时出现问题，出现问题的原因为 {}", e.getMessage());
		}

		if (StringUtils.isBlank(value)) {
			// 如果还是没有，就从请求cookie里获取
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (StringUtils.equalsIgnoreCase(key, cookie.getName())) {
						value = cookie.getValue();
						break;
					}
				}
			}
		}
		return value;
	}
}
