package com.yishuifengxiao.common.jdbc;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yishuifengxiao.common.jdbc.util.JdbcUtil;

@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({ JdbcTemplateAutoConfiguration.class, JdbcTemplateAutoConfiguration.class })
public class JdbcCoreAutoConfiguration {

	/**
	 * 注入一个JdbcTemplate操作工具
	 * 
	 * @param jdbcTemplate
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(JdbcTemplate.class)
	public JdbcHelper jdbcHelper(JdbcTemplate jdbcTemplate) {
		SimpleJdbcHelper simpleJdbcHelper = new SimpleJdbcHelper();
		simpleJdbcHelper.setJdbcTemplate(jdbcTemplate);
		return simpleJdbcHelper;
	}

	/**
	 * 注入一个 JdbcTemplate操作器工具
	 * 
	 * @param jdbcHelper
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(JdbcHelper.class)
	public JdbcUtil jdbcUtil(JdbcHelper jdbcHelper) {
		return new JdbcUtil(jdbcHelper);
	}
}
