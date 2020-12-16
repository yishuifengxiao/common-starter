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
import com.yishuifengxiao.common.jdbc.sql.DeleteSqlBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleDeleteSqlBuilder;
import com.yishuifengxiao.common.jdbc.translator.DeleteTranslator;
import com.yishuifengxiao.common.tool.collections.DataUtil;

/**
 * 简单实现的删除动作解释器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleDeleteTranslator implements DeleteTranslator {

	private final DeleteSqlBuilder deleteSqlBuilder = new SimpleDeleteSqlBuilder();

	/**
	 * 根据主键删除一条数据
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param clazz           操作对象
	 * @param primaryKey      主键
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int deleteByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, Object primaryKey) {
		SqlData sqlData = deleteSqlBuilder.deleteByPrimaryKey(fieldExtractor, clazz, primaryKey);
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

	/**
	 * 根据条件删除删除
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               删除条件
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int delete(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t) {
		SqlData sqlData = deleteSqlBuilder.delete(fieldExtractor, selective, t);
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

	/**
	 * 根据条件删除删除
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param clazz           操作对象
	 * @param selective       是否为可选属性方式
	 * @param conditions      筛选条件
	 * @return
	 */
	@Override
	public <T> int delete(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, boolean selective, List<Condition> conditions) {
		conditions = DataUtil.stream(conditions).filter(Objects::nonNull)
				.filter(t -> null != t.getType() && null != t.getLink() && StringUtils.isNotBlank(t.getName()))
				.collect(Collectors.toList());
		SqlData sqlData = deleteSqlBuilder.deleteByContion(fieldExtractor, clazz, selective, conditions);
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

}
