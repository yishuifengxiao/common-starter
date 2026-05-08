package com.yishuifengxiao.common.jdbc;


import com.yishuifengxiao.common.tool.jdbc.JdbcHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * JdbcTemplate扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnClass({DataSource.class, JdbcTemplate.class})
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class})
public class JdbcCoreAutoConfiguration {

    /**
     * 注入一个JdbcTemplate操作工具
     *
     * @param jdbcTemplate JdbcTemplate
     * @return JdbcTemplate操作工具
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(JdbcTemplate.class)
    public JdbcHelper jdbcHelper(JdbcTemplate jdbcTemplate) {
        JdbcHelper jdbcHelper = new JdbcHelper();
        jdbcHelper.setJdbcTemplate(jdbcTemplate);
        return jdbcHelper;
    }

    /**
     * 注入一个 JdbcTemplate操作器工具
     *
     * @param jdbcHelper JdbcTemplate操作工具
     * @return JdbcTemplate操作器工具
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(JdbcHelper.class)
    public JdbcUtil jdbcUtil(JdbcHelper jdbcHelper) {
        return new JdbcUtil(jdbcHelper);
    }


    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <JdbcTemplate扩展支持> 相关的配置");
    }
}
