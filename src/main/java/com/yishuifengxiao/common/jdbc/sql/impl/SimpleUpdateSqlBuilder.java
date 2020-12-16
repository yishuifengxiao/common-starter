package com.yishuifengxiao.common.jdbc.sql.impl;

import java.util.ArrayList;
import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.UpdateSqlBuilder;
import com.yishuifengxiao.common.jdbc.sql.condition.ConditionBuilder;
import com.yishuifengxiao.common.jdbc.sql.condition.impl.SimpleConditionBuilder;

/**
 * 默认实现的更新类型的SQL执行对象生成器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleUpdateSqlBuilder implements UpdateSqlBuilder {

	private final ConditionBuilder conditionBuilder = new SimpleConditionBuilder();

	/**
	 * 生成一个根据主键更新的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              待更新的数据
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData updateByPrimaryKey(FieldExtractor fieldExtractor, boolean selective, T t) {

		// 更新语句的前半部分
		SqlData sqlData = this.createSql(fieldExtractor, t, selective);

		// 后半部分
		StringBuilder sql = sqlData.getSql().append(" and ")
				.append(fieldExtractor.extractPrimaryKey(t.getClass()).getSimpleName()).append(" = ? ");
		
		List<Object> data = sqlData.getArgs();
		data.add(fieldExtractor.extractValue(t, fieldExtractor.extractPrimaryKey(t.getClass()).getName()));

		return new SqlData(sql, data);
	}

	/**
	 * 生成一个根据条件更新的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              待更新的数据
	 * @param condition      更新条件【默认为以可选方式】
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData update(FieldExtractor fieldExtractor, boolean selective, T t, T condition) {

		// 更新语句的前半部分
		SqlData sqlData = this.createSql(fieldExtractor, t, selective);

		// 筛选条件
		SqlData contionsql = conditionBuilder.build(fieldExtractor, condition, true);
		StringBuilder sql = sqlData.getSql().append(contionsql.getSql());

		List<Object> data = sqlData.getArgs();
		data.addAll(contionsql.getArgs());
		return new SqlData(sql, data);

	}

	/**
	 * 生成一个根据条件更新的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              待更新的数据【默认为以可选方式】
	 * @param conditions     筛选条件
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData update(FieldExtractor fieldExtractor, boolean selective, T t, List<Condition> conditions) {
		// 更新语句的前半部分
		SqlData sql = this.createSql(fieldExtractor, t, selective);

		// 筛选条件
		SqlData sqlData = conditionBuilder.build(t.getClass(), fieldExtractor, true, conditions);

		List<Object> data = new ArrayList<>();
		data.addAll(sql.getArgs());
		data.addAll(sqlData.getArgs());

		return new SqlData(sql.getSql().append(sqlData.getSql()), data);
	}

	/**
	 * 根据数据生成一个不包含筛选条件的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提起器
	 * @param t              待更新的数据
	 * @param selective      是否为可选属性更新
	 * @return 不包含筛选条件的SQL执行对象
	 */
	private <T> SqlData createSql(FieldExtractor fieldExtractor, T t, boolean selective) {
		StringBuilder sql = new StringBuilder("update ").append(fieldExtractor.extractTableName(t.getClass()))
				.append(" set  ");

		List<Object> data = new ArrayList<>();
		List<FieldValue> fields = fieldExtractor.extractFiled(t.getClass());
		for (FieldValue field : fields) {
			Object value = fieldExtractor.extractValue(t, field.getName());
			if (null == value && selective) {
				continue;
			}
			sql.append(" ").append(field.getSimpleName()).append(" = ? , ");
			data.add(value);
		}
		// 删除最后一个,
		sql.deleteCharAt(sql.lastIndexOf(","));

		sql.append(" where 1=1 ");

		return new SqlData(sql, data);
	}

}
