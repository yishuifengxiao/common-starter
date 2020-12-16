package com.yishuifengxiao.common.jdbc.sql;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 删除类型的SQL执行对象生成器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface DeleteSqlBuilder extends SqlBuilder {
	/**
	 * 生成一个根据主键删除的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param primaryKey     主键
	 * @return SQL执行对象
	 */
	<T> SqlData deleteByPrimaryKey(FieldExtractor fieldExtractor, Class<T> clazz, Object primaryKey);

	/**
	 * 生成一个根据条件删除的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              删除条件
	 * @return SQL执行对象
	 */
	<T> SqlData delete(FieldExtractor fieldExtractor, boolean selective, T t);

	/**
	 * 生成一个根据条件删除的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param selective      是否为可选属性方式
	 * @param conditions     筛选条件
	 * @return SQL执行对象
	 */
	<T> SqlData deleteByContion(FieldExtractor fieldExtractor, Class<T> clazz, boolean selective,
			List<Condition> conditions);
}
