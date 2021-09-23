package com.yishuifengxiao.common.jdbc.translator.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.translator.InsertTranslator;

/**
 * <p>
 * 系统插入动作解释器
 * </p>
 * 负责执行插入相关的操作
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleInsertTranslator implements InsertTranslator {

	/**
	 * 插入一条数据
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
	public <T> int insert(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, boolean selective,
			ExecuteExecutor executeExecutor, T t) {
		List<Object> data = new ArrayList<>();
		List<FieldValue> list = fieldExtractor.extractFiled(t.getClass());
		StringBuilder sql = new StringBuilder("insert into ").append(fieldExtractor.extractTableName(t.getClass()))
				.append(" ( ");
		StringBuilder params = new StringBuilder(" ( ");
		for (FieldValue field : list) {
			// 获取到的属性的对应的值
			Object value = fieldExtractor.extractValue(t, field.getName());
			if (null == value && selective) {
				continue;
			}
			sql.append(field.getSimpleName()).append(" , ");
			params.append("? ,");
			data.add(value);
		}
		// 删除最后一个,
		sql.deleteCharAt(sql.lastIndexOf(","));
		params.deleteCharAt(params.lastIndexOf(","));
		// 拼接好sql

		sql.append(") values ").append(params).append(" ) ");
		return executeExecutor.execute(jdbcTemplate, sql, data);
	}

}
