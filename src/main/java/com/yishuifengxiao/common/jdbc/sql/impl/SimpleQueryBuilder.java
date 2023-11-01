package com.yishuifengxiao.common.jdbc.sql.impl;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.ConditionBuilder;
import com.yishuifengxiao.common.jdbc.sql.QueryBuilder;
import com.yishuifengxiao.common.tool.collections.CollectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 系统查询条件生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleQueryBuilder implements QueryBuilder {

	private final ConditionBuilder conditionBuilder = new SimpleConditionBuilder();

	/**
	 * 生成一个根据主键查询的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param primaryKey     主键值
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData findByPrimaryKey(FieldExtractor fieldExtractor, Class<T> clazz, Object primaryKey) {

		List<FieldValue> list = fieldExtractor.extractFiled(clazz);

		// 查询主体
		StringBuilder sql = conditionBuilder.creatResult(list).append(" from ")
				.append(fieldExtractor.extractTableName(clazz)).append(" where 1=1 ");

		// 查询参数
		FieldValue fieldValue = fieldExtractor.extractPrimaryKey(clazz);

		sql.append(" and ").append(fieldValue.getSimpleName()).append(" = ? ");

		return new SqlData(sql, Arrays.asList(primaryKey));
	}

	/**
	 * 生成一个根据条件查询全部数据的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              查询条件
	 * @param order          排序属性
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData findAll(FieldExtractor fieldExtractor, T t, Order order) {

		List<FieldValue> list = fieldExtractor.extractFiled(t.getClass());

		// 查询主体
		StringBuilder sql = conditionBuilder.creatResult(list).append(" from ")
				.append(fieldExtractor.extractTableName(t.getClass())).append(" where 1=1 ");

		List<Object> data = new ArrayList<>();

		// 查询条件
		SqlData condtion = conditionBuilder.build(fieldExtractor, t, true);
		sql.append(condtion.getSql());

		if (CollectionUtil.isNotEmpty(condtion.getArgs())) {
			data.addAll(condtion.getArgs());
		}

		sql.append(conditionBuilder.createOrder(t.getClass(), fieldExtractor, order));

		return new SqlData(sql, data);
	}

	/**
	 * 生成一个根据条件查询全部数据的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param order          排序属性
	 * @param conditions     筛选条件
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData findAll(FieldExtractor fieldExtractor, Class<T> clazz, Order order, List<Condition> conditions) {

		List<FieldValue> list = fieldExtractor.extractFiled(clazz);

		// 查询主体
		StringBuilder sql = conditionBuilder.creatResult(list).append(" from ")
				.append(fieldExtractor.extractTableName(clazz)).append(" where 1=1 ");

		List<Object> data = new ArrayList<>();

		// 筛选条件
		SqlData condtion = conditionBuilder.build(clazz, fieldExtractor, true, conditions);
		sql.append(condtion.getSql());

		if (CollectionUtil.isNotEmpty(condtion.getArgs())) {
			data.addAll(condtion.getArgs());
		}

		sql.append(conditionBuilder.createOrder(clazz, fieldExtractor, order));

		return new SqlData(sql, data);

	}

	/**
	 * 生成一个根据条件分页查询数据的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              查询条件
	 * @param order          排序属性
	 * @param pageSize       分页大小
	 * @param pageNum        当前页页码
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData findPage(FieldExtractor fieldExtractor, T t, Order order, int pageSize, int pageNum) {

		SqlData sqlData = this.findAll(fieldExtractor, t, order);

		StringBuilder sql = sqlData.getSql().append(" ").append(conditionBuilder.createLimit(pageSize, pageNum));
		return new SqlData(sql, sqlData.getArgs());
	}

	/**
	 * 生成一个根据条件分页查询数据的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param order          排序属性
	 * @param conditions     筛选条件
	 * @param pageSize       分页大小
	 * @param pageNum        当前页页码
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData findPage(FieldExtractor fieldExtractor, Class<T> clazz, Order order, List<Condition> conditions,
			int pageSize, int pageNum) {

		SqlData sqlData = this.findAll(fieldExtractor, clazz, order, conditions);

		StringBuilder sql = sqlData.getSql().append(" ").append(conditionBuilder.createLimit(pageSize, pageNum));
		return new SqlData(sql, sqlData.getArgs());

	}

	/**
	 * 生成一个根据条件查询记录数量的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              查询条件
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData countAll(FieldExtractor fieldExtractor, T t) {
		StringBuilder sql = new StringBuilder("select count(*) from ");

		sql.append(fieldExtractor.extractTableName(t.getClass())).append(" where 1=1 ");

		SqlData condtion = conditionBuilder.build(fieldExtractor, t, true);

		sql.append(condtion.getSql());

		return new SqlData(sql, condtion.getArgs());
	}

	/**
	 * 生成一个根据条件查询记录数量的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param conditions     筛选条件
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData countAll(FieldExtractor fieldExtractor, Class<T> clazz, List<Condition> conditions) {
		StringBuilder sql = new StringBuilder("select count(*) from ");

		sql.append(fieldExtractor.extractTableName(clazz)).append(" where 1=1 ");

		// 筛选条件
		SqlData condtion = conditionBuilder.build(clazz, fieldExtractor, true, conditions);

		sql.append(condtion.getSql());

		return new SqlData(sql, condtion.getArgs());
	}
}
