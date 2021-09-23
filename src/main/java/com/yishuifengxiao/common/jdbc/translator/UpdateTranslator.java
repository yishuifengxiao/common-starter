package com.yishuifengxiao.common.jdbc.translator;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * <p>
 * 更新动作解释器
 * </p>
 * 负责执行更新相关的操作
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */

public interface UpdateTranslator extends ExecuteTranslator {

	/**
	 * 根据主键更新一条数据
	 * 
	 * @param <T>             待更新的数据数据类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @return 受影响的记录的数量
	 */
	<T> int updateByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, boolean selective, T t);

	/**
	 * 根据条件方式批量更新数据
	 * 
	 * @param <T>             待更新的数据数据类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @param condition       更新条件【默认为以可选属性方式】
	 * @return 受影响的记录的数量
	 */
	<T> int update(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t, T condition);

	/**
	 * 根据条件方式批量更新数据
	 * 
	 * @param <T>             待更新的数据数据类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 非查询语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               待更新的数据
	 * @param conditions      筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int update(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t, List<Condition> conditions);

}
