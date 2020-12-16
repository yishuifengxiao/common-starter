package com.yishuifengxiao.common.jdbc.sql.condition.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Condition.Link;
import com.yishuifengxiao.common.jdbc.entity.Condition.Type;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.condition.ConditionBuilder;

/**
 * 默认实现的筛选条件生成器
 * 
 * @author qingteng
 * @date 2020年12月6日
 * @version 1.0.0
 */
public class SimpleConditionBuilder implements ConditionBuilder {

	/**
	 * 根据筛选条件生成SQL执行对象
	 * 
	 * @param <T>
	 * @param clazz          待操作的对象
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param conditions     筛选条件
	 * @return SQL执行对象
	 */
	@Override
	public <T> SqlData build(Class<T> clazz, FieldExtractor fieldExtractor, boolean selective,
			List<Condition> conditions) {
		if (null == conditions) {
			return new SqlData(new StringBuilder(""), new ArrayList<>());
		}
		StringBuilder sql = new StringBuilder();
		List<Object> data = new ArrayList<>();
		for (Condition condition : conditions) {
			if (null == condition || null == condition.getLink() || null == condition.getType()) {
				continue;
			}
			extract(clazz, fieldExtractor, selective, sql, data, condition);
		}

		return new SqlData(sql, data);
	}

	/**
	 * 解析比较条件
	 * 
	 * @param <T>
	 * @param clazz          操作的对象
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选方式
	 * @param sql            sql语句
	 * @param data           待比较数据
	 * @param condition      比较条件
	 */
	private <T> void extract(Class<T> clazz, FieldExtractor fieldExtractor, boolean selective, StringBuilder sql,
			List<Object> data, Condition condition) {

		// 如果为模糊查询，无论怎么样都会检查数据的值
		selective = Type.LIKE == condition.getType() ? true : selective;

		if (this.isSkip(selective, condition.getValue())) {
			return;
		}

		sql.append(Link.AND == condition.getLink() ? " and " : " or  ");
		switch (condition.getType()) {
		case EQUAL:
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" = ? ");
			data.add(condition.getValue());
			break;
		case NOT_EQUAL:
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" <> ? ");
			data.add(condition.getValue());
			break;
		case GREATER:
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" > ? ");
			data.add(condition.getValue());
			break;
		case GREATER_EQUAL:
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" >= ? ");
			data.add(condition.getValue());
			break;
		case LESS:
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" < ? ");
			data.add(condition.getValue());
			break;
		case LESS_EQUAL:
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" <= ? ");
			data.add(condition.getValue());
			break;
		case LIKE:
			if (this.isSkip(true, condition.getValue())) {
				break;
			}
			sql.append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(" like '%")
					.append(condition.getValue()).append("%'");
			break;
		case IS_NULL:
			sql.append(" isnull(").append(fieldExtractor.extractColNameByName(clazz, condition.getName())).append(") ");
			break;
		case NOT_NULL:
			sql.append(" !isnull(").append(fieldExtractor.extractColNameByName(clazz, condition.getName()))
					.append(") ");
			break;
		default:
			break;
		}
	}

	/**
	 * 根据数据生成适用于筛选条件部分的SQL执行对象
	 * 
	 * @param <T>
	 * @param t         数据
	 * @param selective 是否为可选属性方式
	 * @return 适用于筛选条件部分的SQL执行对象
	 */
	@Override
	public <T> SqlData build(FieldExtractor fieldExtractor, T t, boolean selective) {

		StringBuilder sql = new StringBuilder(" ");
		if (null == t) {
			return new SqlData(sql, new ArrayList<>());
		}
		List<Object> args = new ArrayList<>();
		if (null != t) {
			for (FieldValue field : fieldExtractor.extractFiled(t.getClass())) {
				// 获取到的属性的对应的值
				Object value = fieldExtractor.extractValue(t, field.getName());

				if (this.isSkip(selective, value)) {
					// 排除null值和空字符串
					continue;

				}
				sql.append(" and ").append(field.getSimpleName()).append(" = ? ");
				args.add(value);
			}
		}

		return new SqlData(sql, args);

	}

	/**
	 * 是否跳过该属性<br/>
	 * 如果是可选属性方式且属性值为null或空字符串就跳过该属性
	 * 
	 * @param selective 是否为可选属性方式
	 * @param value     属性值
	 * @return true表示跳过该属性，false表示不跳过
	 */
	private boolean isSkip(boolean selective, Object value) {
		if (selective) {
			if (null == value) {
				return true;
			}
			return StringUtils.isBlank(value.toString());
		}

		return false;

	}

}
