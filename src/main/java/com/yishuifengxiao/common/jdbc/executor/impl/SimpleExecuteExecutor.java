package com.yishuifengxiao.common.jdbc.executor.impl;

import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.mapper.ColumnNameRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 系统语句执行器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleExecuteExecutor implements ExecuteExecutor {

	/**
	 * 执行非查询语句
	 * 
	 * @param jdbcTemplate JdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 受影响的记录的数量
	 */
	@Override
	public int execute(JdbcTemplate jdbcTemplate, StringBuilder sql, List<Object> args) {
		log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
		log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql)  ============= start ================ ");
		log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql) 执行的sql语句为 {}", sql);
		log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) 执行的sql语句参数数量为 {} ,参数值为 {}", StringUtils.countMatches(sql, "?"), args);
		int count = jdbcTemplate.update(sql.toString(), this.list2Array(args));
		log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) 执行的sql语句对应的结果为 {}", count);
		log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) ============= end  ================ ");
		log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
		return count;
	}

	/**
	 * 查询数据记录的总条数
	 * 
	 * @param jdbcTemplate JdbcTemplate
	 * @param sql          最终执行的sql语句
	 * @param args         最终执行的sql语句对应的参数
	 * @return 数据记录的总条数
	 */
	@Override
	public long countAll(JdbcTemplate jdbcTemplate, String sql, List<Object> args) {
		log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
		log.trace("【yishuifengxiao-common-spring-boot-starter】(查询数量) ============= start ================ ");
		log.trace("【yishuifengxiao-common-spring-boot-starter】(查询数量) 执行的sql语句为 {}", sql);
		log.trace("【yishuifengxiao-common-spring-boot-starter】(查询数量) 执行的sql语句参数数量为 {} ,参数值为 {}", StringUtils.countMatches(sql, "?"), args);
		long count = jdbcTemplate.queryForObject(sql, Long.class, this.list2Array(args));
		log.trace("【yishuifengxiao-common-spring-boot-starter】(查询数量) 执行的sql语句对应的结果为 {}", count);
		log.trace("【yishuifengxiao-common-spring-boot-starter】(查询数量) ============= end  ================ ");
		log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
		return count;
	}

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, List<Object> args) {
		log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
		log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) ============= start ================ ");
		log.trace("【yishuifengxiao-common-spring-boot-starter】   (查询记录) 执行的sql语句为 {}", sql);
		log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) 执行的sql语句参数数量为 {} ,参数值为 {}", StringUtils.countMatches(sql, "?"), args);
		List<T> list = jdbcTemplate.query(sql, new ColumnNameRowMapper(clazz), this.list2Array(args));
		log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) 执行的sql语句对应的记录的数量为 {} ,对应的结果为 {}", null == list ? 0 : list.size(), list);
		log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) ============= end  ================ ");
		log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
		return list;
	}

	/**
	 * 将链表转换成数组
	 * 
	 * @param args 需要的转换的链表
	 * @return 转换后的数组
	 */
	private Object[] list2Array(List<Object> args) {
		if (null == args) {
			return new Object[] {};
		}

		Object[] array = new Object[args.size()];

		for (int i = 0; i < args.size(); i++) {
			array[i] = args.get(i);
		}
		return array;
	}

}
