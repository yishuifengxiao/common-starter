package com.yishuifengxiao.common.jdbc.translator;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * 插入动作解释器<br/>
 * 负责执行插入相关的操作
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface InsertTranslator extends ExecuteTranslator {

	/**
	 * 插入一条数据
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待插入的数据
	 * @return 受影响的记录的数量
	 */
	<T> int insert(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, boolean selective,
			ExecuteExecutor executeExecutor, T t);

}
