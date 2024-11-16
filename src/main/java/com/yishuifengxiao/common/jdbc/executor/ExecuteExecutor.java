package com.yishuifengxiao.common.jdbc.executor;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

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
    int execute(JdbcTemplate jdbcTemplate, String sql, Object[] args);


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
    <T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, Object args);

    /**
     * 执行sql并返回数据主键
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param fieldValues  最终执行的sql语句对应的参数
     * @return 据主键
     */
    KeyHolder update(JdbcTemplate jdbcTemplate, String sql, List<FieldValue> fieldValues);

    /**
     * 批量保存数据
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param types        数据类型
     * @param parameters   最终执行的sql语句对应的参数
     */
    void batchUpdate(JdbcTemplate jdbcTemplate, String sql, int[] types, List<Object[]> parameters);


}
