package com.yishuifengxiao.common.jdbc.sql.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Condition.Link;
import com.yishuifengxiao.common.jdbc.entity.Condition.Type;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.entity.Order.Direction;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.ConditionBuilder;
import com.yishuifengxiao.common.tool.collections.EmptyUtil;
import com.yishuifengxiao.common.tool.lang.NumberUtil;

/**
 * 系统筛选条件生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleConditionBuilder implements ConditionBuilder {

	/**
	 * 根据筛选条件生成SQL执行对象
	 * 
	 * @param <T>            待操作的对象的类型
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
		if (Type.LIKE == condition.getType()) {
			selective = true;
		}

		if (this.isSkip(selective, condition.getValue())) {
			return;
		}

		String colName = fieldExtractor.extractColNameByName(clazz, condition.getName());
		if (StringUtils.isBlank(colName)) {
			return;
		}
		String link = Link.AND == condition.getLink() ? " and " : " or  ";
		switch (condition.getType()) {
		case EQUAL:
			sql.append(link).append(colName).append(" = ? ");
			data.add(condition.getValue());
			break;
		case NOT_EQUAL:
			sql.append(link).append(colName).append(" <> ? ");
			data.add(condition.getValue());
			break;
		case GREATER:
			sql.append(link).append(colName).append(" > ? ");
			data.add(condition.getValue());
			break;
		case GREATER_EQUAL:
			sql.append(link).append(colName).append(" >= ? ");
			data.add(condition.getValue());
			break;
		case LESS:
			sql.append(link).append(colName).append(" < ? ");
			data.add(condition.getValue());
			break;
		case LESS_EQUAL:
			sql.append(link).append(colName).append(" <= ? ");
			data.add(condition.getValue());
			break;
		case LIKE:
			sql.append(link).append(colName).append(" like '%").append(condition.getValue()).append("%' ");
			break;
		case IS_NULL:
			sql.append(link).append(" isnull(").append(colName).append(") ");
			break;
		case NOT_NULL:
			sql.append(link).append(" !isnull(").append(colName).append(") ");
			break;
		case IN:
			SqlData fragment = this.inContion(colName, condition.getValue());
			if (null == fragment) {
				break;
			}
			sql.append(link).append(fragment.getSqlString());
			data.addAll(fragment.getArgs());
			break;
		default:
			break;
		}
	}

	/**
	 * 解析in连接条件
	 * 
	 * @param colName 数据库列名
	 * @param value   比较值
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	private SqlData inContion(String colName, Object value) {
		if (null == value) {
			return null;
		}
		StringBuilder sql = new StringBuilder(" ( ");
		List params = new ArrayList();
		if (value instanceof List) {
			params = (List) value;
		} else if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			params = Arrays.asList(array);
		}
		if (null == params || params.isEmpty()) {
			return null;
		}
		List<Object> data = new ArrayList<>();
		for (Object param : params) {
			if (null == param || StringUtils.isBlank(param.toString())) {
				continue;
			}
			sql.append(colName).append(" = ? or ");
			data.add(param);
		}
		// 删除最后一个 or
		sql = sql.delete(sql.lastIndexOf("or"), sql.length());
		return new SqlData(sql.append(" ) "), data);
	}

	/**
	 * 根据数据生成适用于筛选条件部分的SQL执行对象
	 * 
	 * @param <T>            数据的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              数据
	 * @param selective      是否为可选属性方式
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

	/**
	 * 根据数据生成一个适用于分页的语句片段
	 * 
	 * @param pageSize 分页大小
	 * @param pageNum  当前页页码
	 * @return 适用于分页的语句片段
	 */
	@Override
	public StringBuilder createLimit(int pageSize, int pageNum) {

		pageSize = NumberUtil.get(pageSize) <= 0 ? 20 : pageSize;
		pageNum = NumberUtil.get(pageNum) <= 0 ? 1 : pageNum;

		return new StringBuilder(" limit  ").append((pageNum - 1) * pageSize).append(" , ").append(pageSize)
				.append("  ");
	}

	/**
	 * 生成适用于排序条件部分的SQL语句片段
	 * 
	 * @param clazz          操作的对象
	 * @param fieldExtractor 属性提取器
	 * @param order          排序条件
	 * @return 适用于排序条件部分的SQL语句片段
	 */
	@Override
	public <T> StringBuilder createOrder(Class<T> clazz, FieldExtractor fieldExtractor, Order order) {

		StringBuilder sql = new StringBuilder(" ");

		if (null == order || StringUtils.isBlank(order.getOrderName())) {

			return sql;
		}

		sql.append("order by ").append(fieldExtractor.extractColNameByName(clazz, order.getOrderName()));
		if (null != order.getDirection()) {
			if (null == order.getDirection() || order.getDirection() == Direction.ASC) {
				sql.append(" asc ");
			} else {
				sql.append(" desc ");
			}
		}

		return sql;

	}

	/**
	 * 根据条件生成适用于查询结果部分的SQL片段
	 * 
	 * @param list POJO对象所有的属性
	 * @return 适用于查询结果部分的SQL片段
	 */
	@Override
	public StringBuilder creatResult(List<FieldValue> list) {

		StringBuilder sql = new StringBuilder("select ");

		if (EmptyUtil.isEmpty(list)) {
			sql.append(" * ");
		} else {
			for (FieldValue field : list) {
				sql.append(" ").append(field.getSimpleName()).append(" as ").append(field.getName()).append(" ")
						.append(",");
			}
			sql.deleteCharAt(sql.lastIndexOf(","));
		}

		return sql;

	}

}
