package com.yishuifengxiao.common.jdbc.translator;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;

/**
 * <p>
 * 删除动作解释器
 * </p>
 * 负责执行删除相关的操作
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DeleteTranslator extends ExecuteTranslator {

	/**
	 * 根据主键删除一条数据
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           需要操作的table对应的POJO类
	 * @param primaryKey      主键
	 * @return 受影响的记录的数量
	 */
	<T> int deleteByPrimaryKey(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor,
			ExecuteExecutor executeExecutor, Class<T> clazz, Object primaryKey);

	/**
	 * 根据条件删除删除
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param selective       是否为可选属性方式
	 * @param t               需要操作的table对应的POJO类实例
	 * @return 受影响的记录的数量
	 */
	<T> int delete(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			boolean selective, T t);

	/**
	 * 根据条件删除删除
	 * 
	 * @param <T>             需要操作的table对应的POJO的类型
	 * @param jdbcTemplate    JdbcTemplate
	 * @param fieldExtractor  属性提取器
	 * @param executeExecutor 语句执行器
	 * @param clazz           需要操作的table对应的POJO类
	 * @param selective       是否为可选属性方式
	 * @param conditions      筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int delete(JdbcTemplate jdbcTemplate, FieldExtractor fieldExtractor, ExecuteExecutor executeExecutor,
			Class<T> clazz, boolean selective, List<Condition> conditions);
}
