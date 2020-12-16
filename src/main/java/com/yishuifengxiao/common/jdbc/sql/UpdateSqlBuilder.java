package com.yishuifengxiao.common.jdbc.sql;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 更新类型的SQL执行对象生成器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface UpdateSqlBuilder extends SqlBuilder {
	/**
	 * 生成一个根据主键更新的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              待更新的数据
	 * @return SQL执行对象
	 */
	<T> SqlData updateByPrimaryKey(FieldExtractor fieldExtractor, boolean selective, T t);

	/**
	 * 生成一个根据条件更新的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              待更新的数据
	 * @param condition      更新条件【默认为以可选方式】
	 * @return SQL执行对象
	 */
	<T> SqlData update(FieldExtractor fieldExtractor, boolean selective, T t, T condition);

	/**
	 * 生成一个根据条件更新的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              待更新的数据
	 * @param conditions      筛选条件
	 * @return SQL执行对象
	 */
	<T> SqlData update(FieldExtractor fieldExtractor, boolean selective, T t, List<Condition> conditions);

}
