package com.yishuifengxiao.common.jdbc.sql.impl;

import java.util.Arrays;
import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.DeleteSqlBuilder;
import com.yishuifengxiao.common.jdbc.sql.condition.ConditionBuilder;
import com.yishuifengxiao.common.jdbc.sql.condition.impl.SimpleConditionBuilder;

/**
 * 默认实现的删除类型的SQL执行对象生成器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleDeleteSqlBuilder implements DeleteSqlBuilder {

	private final ConditionBuilder conditionBuilder = new SimpleConditionBuilder();

	/**
	 * 生成一个根据主键删除的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param clazz          操作对象
	 * @param primaryKey     主键
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData deleteByPrimaryKey(FieldExtractor fieldExtractor, Class<T> clazz, Object primaryKey) {
		StringBuilder sql = createSql(fieldExtractor, clazz);
		sql.append(" and ").append(fieldExtractor.extractPrimaryKey(clazz).getSimpleName()).append(" = ?");
		return new SqlData(sql, Arrays.asList(primaryKey));
	}

	/**
	 * 生成一个根据条件删除的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              删除条件
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData delete(FieldExtractor fieldExtractor, boolean selective, T t) {
		StringBuilder sql = createSql(fieldExtractor, t.getClass());
		SqlData condtion = conditionBuilder.build(fieldExtractor, t, selective);
		return new SqlData(sql.append(condtion.getSqlString()), condtion.getArgs());
	}

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
	@Override
	public <T> SqlData deleteByContion(FieldExtractor fieldExtractor, Class<T> clazz, boolean selective,
			List<Condition> conditions) {

		StringBuilder sql = createSql(fieldExtractor, clazz);

		// 筛选条件
		SqlData sqlData = conditionBuilder.build(clazz, fieldExtractor, selective, conditions);

		return new SqlData(sql.append(sqlData.getSql()), sqlData.getArgs());
	}

	/**
	 * 生成基础的SQL语句
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param clazz          待操作的对象
	 * @return
	 */
	private <T> StringBuilder createSql(FieldExtractor fieldExtractor, Class<T> clazz) {
		StringBuilder sql = new StringBuilder("delete from  ").append(fieldExtractor.extractTableName(clazz))
				.append(" where 1=1  ");
		return sql;
	}

}
