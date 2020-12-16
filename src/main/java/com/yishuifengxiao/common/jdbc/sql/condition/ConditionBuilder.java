package com.yishuifengxiao.common.jdbc.sql.condition;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 筛选条件生成器
 * 
 * @author qingteng
 * @date 2020年12月6日
 * @version 1.0.0
 */
public interface ConditionBuilder {

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
	<T> SqlData build(Class<T> clazz, FieldExtractor fieldExtractor, boolean selective, List<Condition> conditions);

	/**
	 * 根据数据生成适用于筛选条件部分的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param t              数据
	 * @param selective      是否为可选属性方式
	 * @return 适用于筛选条件部分的SQL执行对象
	 */
	<T> SqlData build(FieldExtractor fieldExtractor, T t, boolean selective);

}
