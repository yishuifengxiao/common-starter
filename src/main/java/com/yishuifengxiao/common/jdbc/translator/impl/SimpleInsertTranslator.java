package com.yishuifengxiao.common.jdbc.translator.impl;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.InsertSqlBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleInsertSqlBuilder;
import com.yishuifengxiao.common.jdbc.translator.InsertTranslator;

/**
 * 简单实现的插入动作解释器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleInsertTranslator implements InsertTranslator {
	private final InsertSqlBuilder insertSqlBuilder = new SimpleInsertSqlBuilder();

	/**
	 * 插入一条数据
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待插入的数据
	 * @return 受影响的记录的数量
	 */
	@Override
	public <T> int insert(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, boolean selective,
			ExecuteExecutor executeExecutor, T t) {
		SqlData sqlData = insertSqlBuilder.insert(fieldExtractor, selective, t);
		return executeExecutor.execute(jdbcTemplate, sqlData.getSqlString(), sqlData.getSqlArgs());
	}

}
