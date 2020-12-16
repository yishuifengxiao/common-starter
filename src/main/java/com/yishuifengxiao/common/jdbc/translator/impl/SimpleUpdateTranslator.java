package com.yishuifengxiao.common.jdbc.translator.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.UpdateSqlBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleUpdateSqlBuilder;
import com.yishuifengxiao.common.jdbc.translator.UpdateTranslator;
import com.yishuifengxiao.common.tool.collections.DataUtil;

/**
 * 默认实现的更新动作解释器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleUpdateTranslator implements UpdateTranslator {

	private final UpdateSqlBuilder updateSqlBuilder = new SimpleUpdateSqlBuilder();

	/**
	 * 根据主键更新一条数据
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int updateByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, boolean selective, T t) {
		SqlData sqlData = updateSqlBuilder.updateByPrimaryKey(fieldExtractor, selective, t);
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

	/**
	 * 根据条件方式批量更新数据
	 * 
	 * @param <T>
	 * @param jdbcTemplate
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
		SqlData sqlData = updateSqlBuilder.update(fieldExtractor, selective, t, condition);
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

	/**
	 * 根据条件方式批量更新数据
	 * 
	 * @param <T>
	 * @param jdbcTemplate
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

		SqlData sqlData = updateSqlBuilder.update(fieldExtractor, selective, t, conditions);
		
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

}
