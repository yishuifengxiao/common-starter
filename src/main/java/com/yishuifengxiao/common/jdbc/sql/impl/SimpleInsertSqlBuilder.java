package com.yishuifengxiao.common.jdbc.sql.impl;

import java.util.ArrayList;
import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.InsertSqlBuilder;

/**
 * 简单实现插入类型的SQL执行对象生成器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class SimpleInsertSqlBuilder implements InsertSqlBuilder {

	/**
	 * 生成一个插入的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              插入的数据
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData insert(FieldExtractor fieldExtractor, boolean selective, T t) {
		return createSql(fieldExtractor, t, selective);
	}

	/**
	 * 根据条件生成SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param t              插入的数据
	 * @param selective      是否为可选属性插入
	 * @return
	 */
	private <T> SqlData createSql(FieldExtractor fieldExtractor, T t, boolean selective) {
		List<Object> data = new ArrayList<>();
		List<FieldValue> list = fieldExtractor.extractFiled(t.getClass());
		StringBuilder sql = new StringBuilder("insert into ").append(fieldExtractor.extractTableName(t.getClass())).append(" ( ");
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

		return new SqlData(sql, data);
	}

}
