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
 * JPA操作扩展支持工具
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class JpaUtil {

	/**
	 * 生成根据条件查询的忽略大小和空字段的模糊查询jpa查询条件
	 * 
	 * @param <T> 查询数据对应的数据类型
	 * @param t   查询数据(POJO类)
	 * @return 忽略大小和空字段的模糊查询jpa查询条件
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
	 * @param <T> 查询数据对应的数据类型
	 * @param t   查询数据(POJO类)
	 * @return 忽略大小和空字段的精确查询jpa查询条件
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
	 * 根据查询条件查询出全部数据
	 * 
	 * @param <T>        查询条件对应的POJO类的类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @return 查询出的数据
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> all(@SuppressWarnings("rawtypes") JpaRepositoryImplementation repository,
			Example<T> example) {
		return repository.findAll(example);
	}

	/**
	 * 根据查询条件查询出全部数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @param orderName  排序字段名字，默认升序
	 * @return 查询出的数据
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> all(@SuppressWarnings("rawtypes") JpaRepositoryImplementation repository,
			Example<T> example, String orderName) {
		return repository.findAll(example, Sort.by(new Sort.Order(Sort.Direction.ASC, orderName)));
	}

	/**
	 * 根据查询条件查询出全部数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @param sort       排序条件
	 * @return 查询出的数据
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> all(@SuppressWarnings("rawtypes") JpaRepositoryImplementation repository,
			Example<T> example, Sort sort) {
		return repository.findAll(example, sort);
	}

	/**
	 * 根据条件查询<strong>模糊查询</strong>出所有的数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @return 查询出的数据
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> allFuzzy(@SuppressWarnings("rawtypes") JpaRepositoryImplementation repository, T query) {
		Example<T> example = JpaUtil.fuzzy(query);
		return repository.findAll(example);
	}

	/**
	 * 根据条件查询<strong>模糊查询</strong>出所有的数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param orderName  排序字段名字，默认升序
	 * @return 查询出的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query, String orderName) {
		Example<T> example = JpaUtil.fuzzy(query);
		return repository.findAll(example, Sort.by(new Sort.Order(Sort.Direction.ASC, orderName)));
	}

	/**
	 * 根据条件查询<strong>模糊查询</strong>出所有的数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param sort       排序条件
	 * @return 查询出的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> allFuzzy(JpaRepositoryImplementation repository, T query, Sort sort) {
		Example<T> example = JpaUtil.fuzzy(query);
		return repository.findAll(example, sort);
	}

	/**
	 * 根据条件查询<strong>精确查询</strong>出所有的数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @return 查询出的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query) {
		Example<T> example = exact(query);
		return repository.findAll(example);
	}

	/**
	 * 根据条件查询<strong>精确查询</strong>出所有的数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param orderName  排序字段名字，默认升序
	 * @return 查询出的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query, String orderName) {
		Example<T> example = exact(query);
		return repository.findAll(example, Sort.by(new Sort.Order(Sort.Direction.ASC, orderName)));
	}

	/**
	 * 根据条件查询<strong>精确查询</strong>出所有的数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param sort       排序条件
	 * @return 查询出的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> allExact(JpaRepositoryImplementation repository, T query, Sort sort) {
		Example<T> example = exact(query);
		return repository.findAll(example, sort);
	}

	/**
	 * 根据条件<strong>分页模糊查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum) {
		Example<T> example = JpaUtil.fuzzy(query);
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));

	}

	/**
	 * 根据条件<strong>分页模糊查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @param orderName  排序字段名字，默认升序
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum,
			String orderName) {
		Example<T> example = JpaUtil.fuzzy(query);
		return JpaPage.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, Sort.Direction.ASC, orderName)));

	}

	/**
	 * 根据条件<strong>分页模糊查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param query      查询条件(POJO类实例)
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @param sort       排序条件
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Page<T> pageFuzzy(QueryByExampleExecutor repository, T query, int pageSize, int pageNum,
			Sort sort) {
		Example<T> example = JpaUtil.fuzzy(query);
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, sort)));

	}

	/**
	 * 根据条件<strong>分页精确查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum) {
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize)));
	}

	/**
	 * 根据条件<strong>分页精确查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @param orderName  排序字段名字，默认升序
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum,
			String orderName) {
		return JpaPage.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, Sort.Direction.ASC, orderName)));
	}

	/**
	 * 根据条件<strong>分页精确查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @param sort       排序条件
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum,
			Sort sort) {
		return JpaPage.of(repository.findAll(example, PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, sort)));
	}

	/**
	 * 根据条件<strong>分页精确查询</strong>出数据
	 * 
	 * @param <T>        查询数据对应的数据类型
	 * @param repository JpaRepository实例
	 * @param example    查询条件
	 * @param pageSize   分页大小
	 * @param pageNum    当前页页码,从1开始
	 * @param direction  排序方向
	 * @param properties 排序属性
	 * @return 查询出来的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Page<T> page(QueryByExampleExecutor repository, Example<T> example, int pageSize, int pageNum,
			Direction direction, String... properties) {
		return JpaPage.of(repository.findAll(example,
				PageRequest.of(pageNum > 1 ? pageNum - 1 : 0, pageSize, direction, properties)));
	}

}
