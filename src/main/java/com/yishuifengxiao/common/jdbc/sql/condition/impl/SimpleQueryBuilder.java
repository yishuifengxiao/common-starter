package com.yishuifengxiao.common.jdbc.sql.condition.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.entity.Order.Direction;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.sql.condition.QueryBuilder;
import com.yishuifengxiao.common.tool.collections.EmptyUtil;
import com.yishuifengxiao.common.tool.utils.NumberUtil;

/**
 * 默认实现的查询条件生成器
 * 
 * @author qingteng
 * @date 2020年12月6日
 * @version 1.0.0
 */
public class SimpleQueryBuilder implements QueryBuilder {

	/**
	 * 根据数据生成一个适用于分页的语句
	 * 
	 * @param pageSize 分页大小
	 * @param pageNum  当前页页码
	 * @return
	 */
	@Override
	public StringBuilder createLimit(Integer pageSize, Integer pageNum) {

		pageSize = NumberUtil.get(pageSize) <= 0 ? 20 : pageSize;
		pageNum = NumberUtil.get(pageNum) <= 0 ? 1 : pageNum;

		return new StringBuilder(" limit  ").append((pageNum - 1) * pageSize).append(" , ").append(pageNum * pageSize)
				.append("  ");
	}

	/**
	 * 生成适用于排序条件部分的SQL语句
	 * 
	 * @param clazz          操作的对象
	 * @param fieldExtractor 属性提取器
	 * @param order          排序条件
	 * @return 适用于排序条件部分的SQL语句
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
	 * 根据条件生成适用于查询结果部分的SQL
	 * 
	 * @param <T>
	 * @param list POJO对象所有的属性
	 * @return 适用于查询结果部分的SQL
	 */
	@Override
	public <T> StringBuilder creatResult(List<FieldValue> list) {

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
