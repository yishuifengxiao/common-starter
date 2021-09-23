package com.yishuifengxiao.common.jdbc.translator.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.ConditionBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleConditionBuilder;
import com.yishuifengxiao.common.jdbc.translator.DeleteTranslator;
import com.yishuifengxiao.common.tool.collections.DataUtil;

/**
 * <p>
 * 系统删除动作解释器
 * </p>
 * 负责执行删除相关的操作
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleDeleteTranslator implements DeleteTranslator {

	private final ConditionBuilder conditionBuilder = new SimpleConditionBuilder();

	/**
	 * 根据主键删除一条数据
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           需要操作的table对应的POJO类
	 * @param primaryKey      主键
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int deleteByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, Object primaryKey) {
		StringBuilder sql = createSql(fieldExtractor, clazz);
		sql.append(" and ").append(fieldExtractor.extractPrimaryKey(clazz).getSimpleName()).append(" = ?");
		return executeExecutor.execute(jdbcTemplate, sql, Arrays.asList(primaryKey));
	}

	/**
	 * 根据条件删除删除
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               需要操作的table对应的POJO类实例
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int delete(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t) {
		StringBuilder sql = createSql(fieldExtractor, t.getClass());
		SqlData condtion = conditionBuilder.build(fieldExtractor, t, selective);
		return executeExecutor.execute(jdbcTemplate, sql.append(condtion.getSqlString()), condtion.getArgs());
	}

	/**
	 * 根据条件删除删除
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           需要操作的table对应的POJO类
	 * @param selective       是否为可选属性方式
	 * @param conditions      筛选条件
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int delete(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, boolean selective, List<Condition> conditions) {
		conditions = DataUtil.stream(conditions).filter(Objects::nonNull)
				.filter(t -> null != t.getType() && null != t.getLink() && StringUtils.isNotBlank(t.getName()))
				.collect(Collectors.toList());

		StringBuilder sql = createSql(fieldExtractor, clazz);

		// 筛选条件
		SqlData sqlData = conditionBuilder.build(clazz, fieldExtractor, selective, conditions);

		return executeExecutor.execute(jdbcTemplate, sql.append(sqlData.getSql()), sqlData.getArgs());
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
