package com.yishuifengxiao.common.jdbc.executor;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * <p>
 * 语句执行器
 * </p>
 * 主要功能时根据生成的sql语句和参数执行sql任务并返回最终的执行结果
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ExecuteExecutor {

	/**
	 * 执行非查询语句
	 * 
	 * @param jdbcTemplate JdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 受影响的记录的数量
	 */
	int execute(JdbcTemplate jdbcTemplate, StringBuilder sql, List<Object> args);

	/**
	 * 查询数据记录的总条数
	 * 
	 * @param jdbcTemplate JdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 数据记录的总条数
	 */
	long countAll(JdbcTemplate jdbcTemplate, String sql, List<Object> args);

	/**
	 * 查询所有的符合条件的记录
	 * 
	 * @param <T>          查询的结果数据的类型
	 * @param jdbcTemplate JdbcTemplate
	 * @param clazz        查询的数据的类型
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 所有的符合条件的记录
	 */
	<T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, List<Object> args);

}
