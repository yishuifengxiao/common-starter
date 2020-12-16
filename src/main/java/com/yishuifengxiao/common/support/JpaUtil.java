package com.yishuifengxiao.common.support;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.yishuifengxiao.common.tool.entity.JpaPage;
import com.yishuifengxiao.common.tool.entity.Page;

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
		//@formatter:off 
		ExampleMatcher matcher = ExampleMatcher
				// 构建查询对象
				.matching()
				// 改变默认字符串匹配方式：模糊查询
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) 
				// 忽略空字段
				.withNullHandler(ExampleMatcher.NullHandler.IGNORE)
				// 改变默认大小写忽略方式：忽略大小写
				.withIgnoreCase(true); 
		Example<T> example = Example.of(t, matcher);
		//@formatter:on  
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
		//@formatter:off 
		ExampleMatcher matcher = ExampleMatcher
				// 构建查询对象
				.matching()
				 // 改变默认字符串匹配方式：精确匹配
				.withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT)
				// 忽略空字段
				.withNullHandler(ExampleMatcher.NullHandler.IGNORE)
				 // 改变默认大小写忽略方式：不忽略大小写
				.withIgnoreCase(false);
		Example<T> example = Example.of(t, matcher);
		//@formatter:on  
		return example;
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
		return repository.findAll(example, Sort.by(new Sort.Order(Sort.Direction.ASC, orderName)));
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
		return repository.findAll(example, Sort.by(new Sort.Order(Sort.Direction.ASC, orderName)));
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
		return repository.findAll(example, Sort.by(new Sort.Order(Sort.Direction.ASC, orderName)));
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
	public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum) {
		Example<T> example = JpaUtil.fuzzy(query);
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));

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
	public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum,
			String orderName) {
		Example<T> example = JpaUtil.fuzzy(query);
		return JpaPage.of(repository.findAll(example,
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
	public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum,
			Sort sort) {
		Example<T> example = JpaUtil.fuzzy(query);
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, sort)));

	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum) {
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @param orderName  排序字段【升序】
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum,
			String orderName) {
		return JpaPage.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, Sort.Direction.ASC, orderName)));
	}

	/**
	 * 根据条件查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @param sort       排序条件
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum,
			Sort sort) {
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, sort)));
	}

	/**
	 * 根据条件分页查询数据
	 * 
	 * @param <T>
	 * @param repository
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    分页数
	 * @param direction  排序方向
	 * @param properties 属性
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum,
			Direction direction, String... properties) {
		return JpaPage.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, direction, properties)));
	}

}
