/**
 * 
 */
package com.yishuifengxiao.common.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import com.yishuifengxiao.common.dao.AncestorDao;

import tk.mybatis.mapper.common.Mapper;

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
	 * 默认的第一个元素的索引
	 */
	public final static int FIRST_ELEMENT_INDEX = 0;

	/**
	 * 将项目中所有的JpaRepository收集起来
	 */
	@Autowired(required = false)
	protected Map<String, Repository<?, ?>> repositorys;
	/**
	 * 收集系统中所有的dao类
	 */
	@Autowired(required = false)
	protected Map<String, AncestorDao> daos;

	/**
	 * 收集tkmybatis中的通用mapper
	 */
	@Autowired(required = false)
	protected Map<String, Mapper<?>> mappers;

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
	 * 根据Repository的Class名字获取的Repository的实例化对象
	 * 
	 * @param repositoryName
	 *            Repository的名字
	 * @return Repository的实例化对象
	 */
	protected <T extends Repository<?, ?>> T repository(Class<T> clazz) {
		return repository(toLowerCaseFirstOne(clazz.getSimpleName()));
	}

	/**
	 * 根据Repository的名字获取的Repository的实例化对象
	 * 
	 * @param name
	 *            Repository的名字
	 * @return Repository的实例化对象
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Repository<?, ?>> T repository(String name) {
		if (repositorys == null) {
			return null;
		}
		// 获取到类名
		Repository<?, ?> repository = repositorys.getOrDefault(name, null);
		return repository != null ? (T) repository : null;
	}

	/**
	 * 根据dao实例的Class名字获取dao实例化对象
	 * 
	 * @param clazz
	 *            dao实例Class类名
	 * @return dao实例化对象
	 */
	protected <T extends AncestorDao> T dao(Class<T> clazz) {
		return dao(toLowerCaseFirstOne(clazz.getSimpleName()));
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
		if (daos == null) {
			return null;
		}
		AncestorDao dao = daos.getOrDefault(name, null);
		return dao == null ? null : (T) dao;
	}

	/**
	 * 根据通用mapper的实例名字获取其实例化对象
	 * 
	 * @param name
	 *            通用mapper的实例名字
	 * @return 通用mapper的实例对象
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Mapper<?>> T mapper(String name) {
		if (mappers == null) {
			return null;
		}
		Mapper<?> mapper = mappers.getOrDefault(name, null);
		return mapper == null ? null : (T) mapper;
	}

	/**
	 * 根据通用mapper的实例Class名字获取其实例化对象
	 * 
	 * @param name
	 *            通用mapper的实例Class名字
	 * @return 通用mapper的实例对象
	 */
	protected <T extends Mapper<?>> T mapper(Class<T> clazz) {
		return mapper(toLowerCaseFirstOne(clazz.getSimpleName()));
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
