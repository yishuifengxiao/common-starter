package com.yishuifengxiao.common.base;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * 通用公共父类
 * 
 * @version 1.0.0
 * @author yishui
 * @date 2019-07-03
 */
public abstract class BaseAware implements ApplicationContextAware, DisposableBean {

	private final static Logger log = LoggerFactory.getLogger(BaseAware.class);
	/**
	 * 默认的当前页的页码
	 */
	public final static int DEFAULT_PAGE_NUM = 0;
	/**
	 * 默认的最小页的页码
	 */
	public final static int MIN_PAGE_NUM = 1;
	/**
	 * 默认的第一个元素的索引
	 */
	public final static int FIRST_ELEMENT_INDEX = 0;

	protected ApplicationContext applicationContext;

	/**
	 * 对传入的参数进行非空处理
	 * 
	 * @param param 传入的参数
	 * @return 处理后的参数
	 */
	protected <T> T convert(T t) {
		return t == null || "".equals(t) || "undefined".equals(t) ? null : t;
	}

	/**
	 * 对字符串进行非空和空格处理
	 * 
	 * @param str 传入的参数
	 * @return 处理后的参数
	 */
	protected String trim(String str) {
		return StringUtils.isNotBlank(str) ? str.trim() : null;
	}

	/**
	 * 对参数进行非空和空格处理，并对undefined值的数据进行过滤
	 * 
	 * @param str
	 * @return
	 */
	protected String undefined(String str) {
		str = trim(str);
		return StringUtils.equalsIgnoreCase(str, "undefined") ? null : str;
	}

	/**
	 * 将字符串转为Double
	 * 
	 * @param str
	 * @return
	 */
	protected Double convert2Double(String str) {
		if (StringUtils.isNumeric(str)) {
			return Double.parseDouble(str);
		}
		return null;
	}

	/**
	 * 将字符串转为 Long
	 * 
	 * @param str
	 * @return
	 */
	protected Long convert2Long(String str) {
		if (StringUtils.isNumeric(str)) {
			return Long.parseLong(str);
		}
		return null;
	}

	/**
	 * 将字符串的首字母变为小写的
	 * 
	 * @param s 字符串
	 * @return
	 */
	protected String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

	/**
	 * 对当前页进行减一转换
	 * 
	 * @param pageSize 当前页
	 * @return
	 */
	protected int convert(Integer pageSize) {
		if (pageSize == null || pageSize < MIN_PAGE_NUM) {
			return DEFAULT_PAGE_NUM;
		}
		return pageSize > DEFAULT_PAGE_NUM ? pageSize - MIN_PAGE_NUM : pageSize;
	}

	/**
	 * 返回工厂中所有定义的实例的名字
	 * 
	 * @return
	 */
	public String[] getBeanDefinitionNames() {
		return applicationContext.getBeanDefinitionNames();
	}

	/**
	 * 根据class获取实例对象
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(Class<? extends T> clazz) {
		Assert.notNull(clazz, "对象名字不能为空");
		return applicationContext.getBean(clazz);
	}

	/**
	 * 将源对象里的属性赋值给目标对象
	 * 
	 * @param <S>    源对象
	 * @param <T>    目标对象
	 * @param source 源对象
	 * @param target 目标对象
	 * @return
	 */
	public <S, T> T copy(S source, T target) {
		if (source == null || target == null) {
			return null;
		}
		BeanUtils.copyProperties(source, target);
		return target;
	}

	/**
	 * 根据对象名字获取对象实例
	 * 
	 * @param <T>
	 * @param beanName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanName) {
		Assert.notNull(beanName, "对象名字不能为空");
		return (T) applicationContext.getBean(beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.debug("======================> baseware 初始化");
		this.applicationContext = applicationContext;

	}

	@Override
	public void destroy() throws Exception {
		log.debug("======================> baseware 销毁");
		if (this.applicationContext != null) {
			this.applicationContext = null;
		}

	}

}
