package com.yishuifengxiao.common.jdbc.sql.condition;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 查询条件生成器
 * 
 * @author qingteng
 * @date 2020年12月6日
 * @version 1.0.0
 */
public interface QueryBuilder {

	/**
	 * 根据数据生成一个适用于分页的语句
	 * 
	 * @param pageSize 分页大小
	 * @param pageNum  当前页页码
	 * @return
	 */
	StringBuilder createLimit(Integer pageSize, Integer pageNum);

	/**
	 * 生成适用于排序条件部分的SQL语句
	 * 
	 * @param clazz          操作的对象
	 * @param fieldExtractor 属性提取器
	 * @param order          排序条件
	 * @return 适用于排序条件部分的SQL语句
	 */
	<T> StringBuilder createOrder(Class<T> clazz, FieldExtractor fieldExtractor, Order order);

	/**
	 * 根据条件生成适用于查询结果部分的SQL
	 * 
	 * @param <T>
	 * @param list POJO对象所有的属性
	 * @return 适用于查询结果部分的SQL
	 */
	<T> StringBuilder creatResult(List<FieldValue> list);
}
