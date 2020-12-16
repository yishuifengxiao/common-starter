package com.yishuifengxiao.common.jdbc.executor.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认实现的语句执行器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
@Slf4j
public class SimpleExecuteExecutor implements ExecuteExecutor {

	/**
	 * 执行非查询语句
	 * 
	 * @param jdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 受影响的记录的数量
	 */
	@Override
	public int execute(JdbcTemplate jdbcTemplate, String sql, Object[] args) {
		log.debug("【易水组件】  (执行sql)  ============= start ================ ");
		log.debug("【易水组件】  (执行sql) 执行的sql语句为 {}", sql);
		log.debug("【易水组件】   (执行sql) 执行的sql语句参数数量为 {} ,参数值为 {}", StringUtils.countMatches(sql, "?"), args);
		int count = jdbcTemplate.update(sql, args);
		log.debug("【易水组件】   (执行sql) 执行的sql语句对应的结果为 {}", count);
		log.debug("【易水组件】   (执行sql) ============= end  ================ ");
		return count;
	}

	/**
	 * 查询数据记录的总条数
	 * 
	 * @param <T>
	 * @param jdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 数据记录的总条数
	 */
	@Override
	public <T> Long countAll(JdbcTemplate jdbcTemplate, String sql, Object[] args) {

		log.debug("【易水组件】(查询数量) ============= start ================ ");
		log.debug("【易水组件】(查询数量) 执行的sql语句为 {}", sql);
		log.debug("【易水组件】(查询数量) 执行的sql语句参数数量为 {} ,参数值为 {}", StringUtils.countMatches(sql, "?"), args);
		Long count = jdbcTemplate.queryForObject(sql, args, Long.class);
		log.debug("【易水组件】(查询数量) 执行的sql语句对应的结果为 {}", count);
		log.debug("【易水组件】(查询数量) ============= end  ================ ");
		return count;
	}

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, Object[] args) {
		log.debug("【易水组件】  (查询记录) ============= start ================ ");
		log.debug("【易水组件】   (查询记录) 执行的sql语句为 {}", sql);
		log.debug("【易水组件】  (查询记录) 执行的sql语句参数数量为 {} ,参数值为 {}", StringUtils.countMatches(sql, "?"), args);
		List<T> list = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(clazz));
		log.debug("【易水组件】  (查询记录) 执行的sql语句对应的记录的数量为 {} ,对应的结果为 {}", null == list ? 0 : list.size(), list);
		log.debug("【易水组件】  (查询记录) ============= end  ================ ");

		return list;
	}

}
