package com.yishuifengxiao.common.jdbc.sql;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 筛选条件生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ConditionBuilder {

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
	<T> SqlData build(Class<T> clazz, FieldExtractor fieldExtractor, boolean selective, List<Condition> conditions);

	/**
	 * 根据数据生成适用于筛选条件部分的SQL执行对象
	 * 
	 * @param <T>            数据的类型
	 * @param fieldExtractor 属性提取器
	 * @param t              数据
	 * @param selective      是否为可选属性方式
	 * @return 适用于筛选条件部分的SQL执行对象
	 */
	<T> SqlData build(FieldExtractor fieldExtractor, T t, boolean selective);

	/**
	 * 根据数据生成一个适用于分页的语句片段
	 * 
	 * @param pageSize 分页大小
	 * @param pageNum  当前页页码
	 * @return 适用于分页的语句片段
	 */
	StringBuilder createLimit(int pageSize, int pageNum);

	/**
	 * 生成适用于排序条件部分的SQL语句片段
	 * @param <T>            数据的类型
	 * @param clazz          操作的对象
	 * @param fieldExtractor 属性提取器
	 * @param order          排序条件
	 * @return 适用于排序条件部分的SQL语句片段
	 */
	<T> StringBuilder createOrder(Class<T> clazz, FieldExtractor fieldExtractor, Order order);

	/**
	 * 根据条件生成适用于查询结果部分的SQL片段
	 * 
	 * @param list POJO对象所有的属性
	 * @return 适用于查询结果部分的SQL片段
	 */
	StringBuilder creatResult(List<FieldValue> list);

}
