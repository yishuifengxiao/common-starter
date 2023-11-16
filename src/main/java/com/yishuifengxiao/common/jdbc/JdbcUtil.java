package com.yishuifengxiao.common.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * <p>
 * JdbcTemplate操作器工具
 * </p>
 * 【注意】在没有特意指出的前提下，所有筛选条件的笔记方式为完全匹配
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class JdbcUtil {

    /**
     * JdbcTemplate操作器
     */
    private static JdbcHelper jdbcHelper;

    /**
     * 构造函数
     *
     * @param jdbcHelper JdbcTemplate
     */
    public JdbcUtil(JdbcHelper jdbcHelper) {
        JdbcUtil.jdbcHelper = jdbcHelper;
    }

    /**
     * 获取JdbcTemplate操作器
     *
     * @return JdbcTemplate操作器
     */
    public static JdbcHelper jdbcHelper() {
        Assert.notNull(JdbcUtil.jdbcHelper, "jdbc工具初始化失败");
        return JdbcUtil.jdbcHelper;
    }

    public static JdbcTemplate jdbcTemplate() {
        Assert.notNull(JdbcUtil.jdbcHelper, "jdbc工具初始化失败");
        return JdbcUtil.jdbcHelper.jdbcTemplate();
    }

}
