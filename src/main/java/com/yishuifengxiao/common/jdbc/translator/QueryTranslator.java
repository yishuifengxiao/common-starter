package com.yishuifengxiao.common.jdbc.translator;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.tool.entity.Page;

/**
 * 查询动作解释器<br/>
 * 负责执行查询相关的操作
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface QueryTranslator extends ExecuteTranslator {

	/**
	 * 根据主键查询一条记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           操作对象
	 * @param primaryKey      主键
	 * @return 查询出来的记录
	 */
	<T> T findByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, Object primaryKey);

	/**
	 * 根据条件查询全部符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param t               查询条件
	 * @param order           排序条件
	 * @return 查询出来的记录
	 */
	<T> List<T> findAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor, T t,
			Order order);

	/**
	 * 根据条件查询全部符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           操作对象
	 * @param conditions      筛选条件
	 * @param order           排序条件
	 * @return 查询出来的记录
	 */
	<T> List<T> findAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, List<Condition> conditions, Order order);

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param t               查询条件
	 * @param order           排序条件
	 * @topNum 查询出的记录的数量
	 * @return 查询出来的记录
	 */
	<T> List<T> findTop(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor, T t,
			Order order, Integer topNum);

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           操作对象
	 * @param conditions      筛选条件
	 * @param order           排序条件
	 * @topNum 查询出的记录的数量
	 * @return 查询出来的记录
	 */
	<T> List<T> findTop(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, List<Condition> conditions, Order order, Integer topNum);

	/**
	 * 根据条件分页查询符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param t               查询条件
	 * @param order           排序条件
	 * @param pageSize        分页大小
	 * @param pageNum         当前页页码
	 * @return 查询出来的记录
	 */
	<T> Page<T> findPage(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor, T t,
			Order order, Integer pageSize, Integer pageNum);

	/**
	 * 根据条件分页查询符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           操作对象
	 * @param conditions      筛选条件
	 * @param order           排序条件
	 * @param pageSize        分页大小
	 * @param pageNum         当前页页码
	 * @return 查询出来的记录
	 */
	<T> Page<T> findPage(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, List<Condition> conditions, Order order, Integer pageSize, Integer pageNum);

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param t               查询条件
	 * @return 符合条件的记录的数量
	 */
	<T> Long countAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor, T t);

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           操作对象
	 * @param conditions      筛选条件
	 * @return 符合条件的记录的数量
	 */
	<T> Long countAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, List<Condition> conditions);

}
