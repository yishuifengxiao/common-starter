package com.yishuifengxiao.common.jdbc.executor;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 语句执行器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface ExecuteExecutor {

	/**
	 * 执行非查询语句
	 * 
	 * @param jdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 受影响的记录的数量
	 */
	int execute(JdbcTemplate jdbcTemplate, String sql, Object[] args);

	/**
	 * 查询数据记录的总条数
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 数据记录的总条数
	 */
	<T> Long countAll(JdbcTemplate jdbcTemplate, String sql, Object[] args);

	/**
	 * 查询所有的符合条件的记录
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param clazz        查询的数据的类型
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 所有的符合条件的记录
	 */
	<T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, Object[] args);

}
