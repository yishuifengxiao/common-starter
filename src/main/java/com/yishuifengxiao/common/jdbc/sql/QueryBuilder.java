package com.yishuifengxiao.common.jdbc.sql;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 查询条件生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface QueryBuilder {

	/**
	 * 生成一个根据主键查询的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param primaryKey     主键值
	 * @return SQL执行对象
	 */
	<T> SqlData findByPrimaryKey(FieldExtractor fieldExtractor, Class<T> clazz, Object primaryKey);

	/**
	 * 生成一个根据条件查询全部数据的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              查询条件
	 * @param order          排序属性
	 * @return SQL执行对象
	 */
	<T> SqlData findAll(FieldExtractor fieldExtractor, T t, Order order);

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
	<T> SqlData findAll(FieldExtractor fieldExtractor, Class<T> clazz, Order order, List<Condition> conditions);

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
	<T> SqlData findPage(FieldExtractor fieldExtractor, T t, Order order, int pageSize, int pageNum);

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
	<T> SqlData findPage(FieldExtractor fieldExtractor, Class<T> clazz, Order order, List<Condition> conditions,
			int pageSize, int pageNum);

	/**
	 * 生成一个根据条件查询记录数量的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              查询条件
	 * @return SQL执行对象
	 */
	<T> SqlData countAll(FieldExtractor fieldExtractor, T t);

	/**
	 * 生成一个根据条件查询记录数量的SQL执行对象
	 * 
	 * @param <T>            操作对象的类型
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param conditions     筛选条件
	 * @return SQL执行对象
	 */
	<T> SqlData countAll(FieldExtractor fieldExtractor, Class<T> clazz, List<Condition> conditions);

}
