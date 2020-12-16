package com.yishuifengxiao.common.jdbc.sql;

import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 插入类型的SQL执行对象生成器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface InsertSqlBuilder extends SqlBuilder {

	/**
	 * 生成一个插入的SQL执行对象
	 * 
	 * @param <T>
	 * @param fieldExtractor 属性提取器
	 * @param selective      是否为可选属性方式
	 * @param t              插入的数据
	 * @return SQL执行对象
	 */
	<T> SqlData insert(FieldExtractor fieldExtractor, boolean selective, T t);
}
