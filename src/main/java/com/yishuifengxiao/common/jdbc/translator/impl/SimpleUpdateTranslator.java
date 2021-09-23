package com.yishuifengxiao.common.jdbc.translator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.ConditionBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleConditionBuilder;
import com.yishuifengxiao.common.jdbc.translator.UpdateTranslator;
import com.yishuifengxiao.common.tool.collections.DataUtil;

/**
 * <p>
 * 系统更新动作解释器
 * </p>
 * 负责执行更新相关的操作
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleUpdateTranslator implements UpdateTranslator {

	private final ConditionBuilder conditionBuilder = new SimpleConditionBuilder();

	/**
	 * 根据主键更新一条数据
	 * 
	 * @param <T>             待更新的数据数据类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int updateByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, boolean selective, T t) {
		// 更新语句的前半部分
		SqlData sqlData = this.createSql(fieldExtractor, t, selective);

		// 后半部分
		StringBuilder sql = sqlData.getSql().append(" and ")
				.append(fieldExtractor.extractPrimaryKey(t.getClass()).getSimpleName()).append(" = ? ");

		List<Object> data = sqlData.getArgs();

		data.add(fieldExtractor.extractValue(t, fieldExtractor.extractPrimaryKey(t.getClass()).getName()));

		return executeExecutor.execute(jdbcTemplate, sql, data);
	}

	/**
	 * 根据条件方式批量更新数据
	 * 
	 * @param <T>             待更新的数据数据类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @param condition       更新条件【默认为以可选属性方式】
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int update(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t, T condition) {
		// 更新语句的前半部分
		SqlData sqlData = this.createSql(fieldExtractor, t, selective);

		// 筛选条件
		SqlData contionsql = conditionBuilder.build(fieldExtractor, condition, true);
		
		
		StringBuilder sql = sqlData.getSql().append(contionsql.getSql());

		List<Object> data = sqlData.getArgs();
		data.addAll(contionsql.getArgs());

		return executeExecutor.execute(jdbcTemplate, sql, data);
	}

	/**
	 * 根据条件方式批量更新数据
	 * 
	 * @param <T>             待更新的数据数据类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @param conditions      筛选条件
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int update(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t, List<Condition> conditions) {

		conditions = DataUtil.stream(conditions).filter(Objects::nonNull)
				.filter(v -> null != v.getType() && null != v.getLink() && StringUtils.isNotBlank(v.getName()))
				.collect(Collectors.toList());

		// 更新语句的前半部分
		SqlData sql = this.createSql(fieldExtractor, t, selective);

		// 筛选条件
		SqlData sqlData = conditionBuilder.build(t.getClass(), fieldExtractor, true, conditions);

		List<Object> data = new ArrayList<>();
		data.addAll(sql.getArgs());
		data.addAll(sqlData.getArgs());

		return executeExecutor.execute(jdbcTemplate, sql.getSql().append(sqlData.getSql()), data);
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
