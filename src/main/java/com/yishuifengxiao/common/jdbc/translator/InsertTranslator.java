package com.yishuifengxiao.common.jdbc.translator;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * <p>
 * 插入动作解释器
 * </p>
 * 负责执行插入相关的操作
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface InsertTranslator extends ExecuteTranslator {

	/**
	 * 插入一条数据
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               需要操作的table对应的POJO类实例
	 * @return 受影响的记录的数量
	 */
	<T> int insert(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, boolean selective,
			ExecuteExecutor executeExecutor, T t);

}
