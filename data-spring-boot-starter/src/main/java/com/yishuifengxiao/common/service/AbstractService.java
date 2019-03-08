/**
 * 
 */
package com.yishuifengxiao.common.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.yishuifengxiao.common.dao.AncestorDao;

/**
 * 逻辑层抽象公有逻辑类
 * 
 * @author yishui
 * @Date 2019年3月8日
 * @version 1.0.0
 */
public abstract class AbstractService {
	/**
	 * 默认的当前页的页码
	 */
	public final static int DEFAULT_PAGE_NUM = 0;
	/**
	 * 默认的最小页的页码
	 */
	public final static int MIN_PAGE_NUM = 1;

	/**
	 * 对当前页进行减一转换
	 * 
	 * @param pageSize
	 *            当前页
	 * @return
	 */
	protected int converNum(Integer pageSize) {
		if (pageSize == null || pageSize < MIN_PAGE_NUM) {
			return DEFAULT_PAGE_NUM;
		}
		return pageSize > DEFAULT_PAGE_NUM ? pageSize - MIN_PAGE_NUM : pageSize;
	}

	/**
	 * 将项目中所有的JpaRepository收集起来
	 */
	@Autowired(required = false)
	protected Map<String, JpaRepositoryImplementation<?, ?>> repositorys;
	/**
	 * 收集系统中所有的dao类
	 */
	@Autowired(required = false)
	protected Map<String, AncestorDao> daseDaos;

	/**
	 * 根据Repository的名字获取的Repository的实例化对象
	 * 
	 * @param repositoryName
	 *            Repository的名字
	 * @return Repository的实例化对象
	 */
	protected <T extends JpaRepositoryImplementation<?, ?>> T repository(Class<T> clazz) {
		return repository(clazz.getSimpleName());
	}

	/**
	 * 根据Repository的名字获取的Repository的实例化对象
	 * 
	 * @param name
	 *            Repository的名字
	 * @return Repository的实例化对象
	 */
	@SuppressWarnings("unchecked")
	protected <T extends JpaRepositoryImplementation<?, ?>> T repository(String name) {
		// 获取到类名
		JpaRepositoryImplementation<?, ?> repository = repositorys.getOrDefault(toLowerCaseFirstOne(name), null);
		return repository != null ? (T) repository : null;
	}

	/**
	 * 根据dao实例的名字获取dao实例化对象
	 * 
	 * @param clazz
	 *            dao实例类名
	 * @return dao实例化对象
	 */
	protected <T extends AncestorDao> T dao(Class<T> clazz) {
		return dao(clazz);
	}

	/**
	 * 根据dao实例的名字获取dao实例化对象
	 * 
	 * @param name
	 *            dao实例的名字
	 * @return dao实例化对象
	 */
	@SuppressWarnings("unchecked")
	protected <T extends AncestorDao> T dao(String name) {
		AncestorDao dao = daseDaos.getOrDefault(toLowerCaseFirstOne(name), null);
		return dao == null ? null : (T) dao;
	}

	/**
	 * 将字符串的首字母变为小写的
	 * 
	 * @param s
	 *            字符串
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
	 * 将字符串的首字母变为大写的
	 * 
	 * @param s
	 *            字符串
	 * @return
	 */
	protected String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

}
