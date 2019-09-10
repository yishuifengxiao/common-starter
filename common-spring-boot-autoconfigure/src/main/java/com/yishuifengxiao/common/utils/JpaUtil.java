package com.yishuifengxiao.common.utils;

import com.yishuifengxiao.common.tool.entity.Page;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

/**
 * jpa工具类
 *
 * @author yishui
 * @version 1.0.0
 * @date 2019-8-28
 */
public class JpaUtil {

	/**
	 * 生成根据条件查询的忽略大小和空字段的模糊查询jpa查询条件
	 *
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T> Example<T> fuzzy(T t) {

		ExampleMatcher matcher = ExampleMatcher.matching()// 构建查询对象
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 改变默认字符串匹配方式：模糊查询
				.withNullHandler(ExampleMatcher.NullHandler.IGNORE)// 忽略空字段
				.withIgnoreCase(true); // 改变默认大小写忽略方式：忽略大小写
		Example<T> example = Example.of(t, matcher);

		return example;
	}

	/**
	 * 生成根据条件查询的忽略大小和空字段的精确查询jpa查询条件
	 *
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T> Example<T> exact(T t) {

		ExampleMatcher matcher = ExampleMatcher.matching()// 构建查询对象
				.withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT) // 改变默认字符串匹配方式：精确匹配
				.withNullHandler(ExampleMatcher.NullHandler.IGNORE)// 忽略空字段
				.withIgnoreCase(false); // 改变默认大小写忽略方式：忽略大小写
		Example<T> example = Example.of(t, matcher);

		return example;
	}

	/**
	 * 根据条件分页查询数据<br/>
	 * 【分页模糊查询】
	 * 
	 * @param repository
	 * @param query      查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Page<T> pageFuzzy(JpaRepositoryImplementation repository, T query, int pageSize, int pageNum) {
		Example<T> example = JpaUtil.fuzzy(query);
		return Page.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));

	}

	/**
	 * 根据条件分页查询数据<br/>
	 * 【分页模糊查询】
	 * 
	 * @param repository
	 * @param query      查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @param orderName  排序字段【升序】
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Page<T> pageFuzzy(JpaRepositoryImplementation repository, T query, int pageSize, int pageNum,
			String orderName) {
		Example<T> example = JpaUtil.fuzzy(query);
		return Page.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, Sort.Direction.ASC, orderName)));

	}

	/**
	 * 根据条件分页查询数据<br/>
	 * 【分页模糊查询】
	 * 
	 * @param <T>
	 * @param repository
	 * @param query      查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @param sort       排序条件
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Page<T> pageFuzzy(JpaRepositoryImplementation repository, T query, int pageSize, int pageNum,
			Sort sort) {
		Example<T> example = JpaUtil.fuzzy(query);
		return Page.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));

	}

	/**
	 * 根据条件查询出所有的数据 【模糊查询】 <br/>
	 * 【全部模糊查询】
	 * 
	 * @param repository
	 * @param query      查询条件
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query) {
		Example<T> example = JpaUtil.fuzzy(query);
		return repository.findAll(example);
	}

	/**
	 * 根据条件查询出所有的数据 <br/>
	 * 【全部模糊查询】
	 * 
	 * @param repository
	 * @param query      查询条件
	 * @param orderName  排序字段【升序】
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query, String orderName) {
		Example<T> example = JpaUtil.fuzzy(query);
		return repository.findAll(example, new Sort(Sort.Direction.ASC, orderName));
	}

	/**
	 * 根据条件模糊查询出所有的数据<br/>
	 * 【全部模糊查询】
	 * 
	 * @param <T>
	 * @param repository
	 * @param query      查询条件
	 * @param sort       排序条件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query, Sort sort) {
		Example<T> example = JpaUtil.fuzzy(query);
		return repository.findAll(example, sort);
	}

	/**
	 * 根据条件查询出所有的数据 【精确查询】 <br/>
	 *
	 * @param repository
	 * @param query      查询条件
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query) {
		Example<T> example = exact(query);
		return repository.findAll(example);
	}

	/**
	 * 根据条件查询出所有的数据 【精确查询】
	 *
	 * @param repository
	 * @param query      查询条件
	 * @param orderName  排序字段【升序】
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query, String orderName) {
		Example<T> example = exact(query);
		return repository.findAll(example, new Sort(Sort.Direction.ASC, orderName));
	}

	/**
	 * 根据条件查询出所有的数据 【精确查询】 <br/>
	 * 
	 * @param <T>
	 * @param repository
	 * @param query      查询条件
	 * @param sort       排序条件
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query, Sort sort) {
		Example<T> example = exact(query);
		return repository.findAll(example, sort);
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(JpaRepositoryImplementation repository, Example<T> example, int pageSize,
			int pageNum) {
		return Page.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param orderName  排序字段【升序】
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(JpaRepositoryImplementation repository, Example<T> example, int pageSize,
			int pageNum, String orderName) {
		return Page.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, Sort.Direction.ASC, orderName)));
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param sort       排序条件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(JpaRepositoryImplementation repository, Example<T> example, int pageSize,
			int pageNum, Sort sort) {
		return Page.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, sort)));
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> all(JpaRepositoryImplementation repository, Example<T> example) {
		return repository.findAll(example);
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param orderName  排序字段【升序】
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> all(JpaRepositoryImplementation repository, Example<T> example, String orderName) {
		return repository.findAll(example, new Sort(Sort.Direction.ASC, orderName));
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param sort       排序条件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> all(JpaRepositoryImplementation repository, Example<T> example, Sort sort) {
		return repository.findAll(example, sort);
	}

}
