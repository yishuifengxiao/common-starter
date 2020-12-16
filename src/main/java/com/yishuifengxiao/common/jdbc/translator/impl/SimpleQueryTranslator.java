package com.yishuifengxiao.common.jdbc.translator.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.QuerySqlBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleQuerySqlBuilder;
import com.yishuifengxiao.common.jdbc.translator.QueryTranslator;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.Page;

/**
 * 简单实现的查询动作解释器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleQueryTranslator implements QueryTranslator {

	private final QuerySqlBuilder querySqlBuilder = new SimpleQuerySqlBuilder();

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
	@Override
	public <T> T findByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, Object primaryKey) {
		SqlData sqlData = querySqlBuilder.findByPrimaryKey(fieldExtractor, clazz, primaryKey);
		return DataUtil
				.first(executeExecutor.findAll(jdbcTemplate, clazz, sqlData.getSqlString(), sqlData.getSqlArgs()));
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, T t, Order order) {

		SqlData sqlData = querySqlBuilder.findAll(fieldExtractor, t, order);

		return (List<T>) executeExecutor.findAll(jdbcTemplate, t.getClass(), sqlData.getSqlString(),
				sqlData.getSqlArgs());
	}

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
	@Override
	public <T> List<T> findAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, List<Condition> conditions, Order order) {

		SqlData sqlData = querySqlBuilder.findAll(fieldExtractor, clazz, order, this.collect(conditions));

		String sql = sqlData.getSqlString();
		Object[] args = sqlData.getSqlArgs();

		return (List<T>) executeExecutor.findAll(jdbcTemplate, clazz, sql, args);

	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findTop(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, T t, Order order, Integer topNum) {
		// 查询出该分页的数据
		SqlData sql = querySqlBuilder.findPage(fieldExtractor, t, order, topNum, 1);

		return (List<T>) executeExecutor.findAll(jdbcTemplate, t.getClass(), sql.getSqlString(), sql.getSqlArgs());
	}

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
	@Override
	public <T> List<T> findTop(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, List<Condition> conditions, Order order, Integer topNum) {

		// 查询出该分页的数据
		SqlData sql = querySqlBuilder.findPage(fieldExtractor, clazz, order, this.collect(conditions), topNum, 1);
		return (List<T>) executeExecutor.findAll(jdbcTemplate, clazz, sql.getSqlString(), sql.getSqlArgs());
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> Page<T> findPage(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, T t, Order order, Integer pageSize, Integer pageNum) {

		// 查询出该分页的数据
		SqlData sql = querySqlBuilder.findPage(fieldExtractor, t, order, pageSize, pageNum);

		List<T> data = (List<T>) executeExecutor.findAll(jdbcTemplate, t.getClass(), sql.getSqlString(),
				sql.getSqlArgs());

		// 查询出总的记录数量
		SqlData countSql = querySqlBuilder.countAll(fieldExtractor, t);
		Long total = executeExecutor.countAll(jdbcTemplate, countSql.getSqlString(), countSql.getSqlArgs());

		return Page.of(data, total, pageSize, pageNum);
	}

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
	@Override
	public <T> Page<T> findPage(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, List<Condition> conditions, Order order, Integer pageSize,
			Integer pageNum) {

		// 查询出该分页的数据
		SqlData sql = querySqlBuilder.findPage(fieldExtractor, clazz, order, this.collect(conditions), pageSize,
				pageNum);
		List<T> data = (List<T>) executeExecutor.findAll(jdbcTemplate, clazz, sql.getSqlString(), sql.getSqlArgs());

		// 查询出总的记录数量
		SqlData countSql = querySqlBuilder.countAll(fieldExtractor, clazz, this.collect(conditions));
		Long total = executeExecutor.countAll(jdbcTemplate, countSql.getSqlString(), countSql.getSqlArgs());

		return Page.of(data, total, pageSize, pageNum);

	}

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
	@Override
	public <T> Long countAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			T t) {
		SqlData sqlData = querySqlBuilder.countAll(fieldExtractor, t);
		return executeExecutor.countAll(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

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
	@Override
	public <T> Long countAll(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, List<Condition> conditions) {
		SqlData sqlData = querySqlBuilder.countAll(fieldExtractor, clazz, this.collect(conditions));
		return executeExecutor.countAll(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

	/**
	 * 对筛选条件进行数据合法性过滤
	 * 
	 * @param conditions 筛选条件
	 * @return 过滤后的筛选条件
	 */
	private List<Condition> collect(List<Condition> conditions) {
		conditions = DataUtil.stream(conditions).filter(Objects::nonNull)
				.filter(t -> null != t.getType() && null != t.getLink() && StringUtils.isNotBlank(t.getName()))
				.collect(Collectors.toList());
		return conditions;
	}

}
