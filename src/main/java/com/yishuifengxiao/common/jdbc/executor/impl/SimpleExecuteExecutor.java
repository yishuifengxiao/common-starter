package com.yishuifengxiao.common.jdbc.executor.impl;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.util.ColumnNameRowMapper;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLType;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统语句执行器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleExecuteExecutor implements ExecuteExecutor {

    private final static Set<String> prefixs = Arrays.asList("java", "javax", "org.omg", "org" +
            ".w3c", "jdk", "sun", "com.sun").stream().collect(Collectors.toSet());

    /**
     * 执行非查询语句
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param args         最终执行的sql语句对应的参数
     * @return 受影响的记录的数量
     */
    @Override
    public int execute(JdbcTemplate jdbcTemplate, String sql, Object... args) {
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql)  ============= start " +
                "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql) 执行的sql语句为 {}", sql);
        log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) 执行的sql语句参数数量为 {} ,参数值为 " + "{}", StringUtils.countMatches(sql, "?"), args);
        int count = (null == args || args.length == 0) ? jdbcTemplate.update(sql) :
                jdbcTemplate.update(sql, args);
        log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) 执行的sql语句对应的结果为 {}", count);
        log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) ============= end  " +
                "================ ");
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, Object args) {
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) ============= start " +
                "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】   (查询记录) 执行的sql语句为 {}", sql);
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) 执行的sql语句参数数量为 {} ,参数值为 {}"
                , StringUtils.countMatches(sql, "?"), args);
        List<T> results = null;

        if (null == args) {
            results = execute(jdbcTemplate, clazz, sql, null, null);
        } else {
            if (args instanceof Map) {
                Map map = (Map) args;
                results = execute(jdbcTemplate, clazz, sql, null, map);
            } else if (args instanceof List) {
                List list = (List) args;
                Object[] params = list.toArray(Object[]::new);
                results = execute(jdbcTemplate, clazz, sql, params, null);
            } else if (args.getClass().isArray()) {
                Object[] params = (Object[]) args;
                results = execute(jdbcTemplate, clazz, sql, params, null);
            } else {
                Object[] params = new Object[]{args};
                results = execute(jdbcTemplate, clazz, sql, params, null);
            }
        }
        results = null == results ? Collections.EMPTY_LIST : results;
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) 执行的sql语句对应的记录的数量为 {} ," +
                "对应的结果为 {}", results.size(), results);
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (查询记录) ============= end  " +
                "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
        return results;
    }

    /**
     * 执行sql并返回数据主键
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param fieldValues  最终执行的sql语句对应的参数
     * @return 据主键
     */
    @Override
    public KeyHolder update(JdbcTemplate jdbcTemplate, String sql, List<FieldValue> fieldValues) {
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql) ============= start " +
                "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】   (执行sql) 执行的sql语句为 {}", sql);
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql) 执行的sql语句参数数量为 {} ,参数值为 " + "{}", StringUtils.countMatches(sql, "?"), fieldValues);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < fieldValues.size(); i++) {
                FieldValue value = fieldValues.get(i);
                Object valueValue = value.getValue();
                SQLType sqlType = value.sqlType();
                if (null != valueValue) {
                    if (null == sqlType || JDBCType.NULL == sqlType || JDBCType.OTHER == sqlType) {
                        ps.setObject(i + 1, valueValue);
                    } else {
                        ps.setObject(i + 1, valueValue, sqlType);
                    }
                } else {
                    ps.setNull(i + 1, sqlType.getVendorTypeNumber());
                }

            }
            return ps;
        }, keyHolder);
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql) 执行的sql语句对应的记录的数量为 {} ," + "对应的主键为 {}", update, keyHolder);
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (执行sql) ============= end  " +
                "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
        return keyHolder;
    }

    /**
     * 批量保存数据
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param types        数据类型
     * @param parameters   最终执行的sql语句对应的参数
     */
    @Override
    public void batchUpdate(JdbcTemplate jdbcTemplate, String sql, int[] types,
                            List<Object[]> parameters) {
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (批量执行sql) ============= start " + "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】   (批量执行sql) 执行的sql语句为 {}", sql);
        BatchSqlUpdate bsu = new BatchSqlUpdate(jdbcTemplate.getDataSource(), sql);
        bsu.setBatchSize(500);
        bsu.setTypes(types);
        parameters.stream().filter(Objects::nonNull).forEach(v -> {
            bsu.update(v);
        });
        bsu.flush();
        log.trace("【yishuifengxiao-common-spring-boot-starter】  (批量执行sql) ============= end  " +
                "================ ");
        log.trace("【yishuifengxiao-common-spring-boot-starter】 \r\n");
    }

    /**
     * 执行查询过程
     *
     * @param jdbcTemplate JdbcTemplate
     * @param clazz        返回数据类型
     * @param sql          待执行的sql
     * @param params       执行参数
     * @param map          命名查询时的参数
     * @param <T>          数据类型
     * @return 查询到的数据
     */
    private <T> List<T> execute(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql,
                                Object[] params, Map<String, Object> map) {
        boolean systemClass =
                prefixs.stream().anyMatch(s -> clazz.getName().toLowerCase().startsWith(s.trim()));
        List<T> results = null;
        if (CollUtil.isEmpty(params) && (null == map || map.isEmpty())) {
            results = systemClass ? jdbcTemplate.queryForList(sql, clazz) :
                    jdbcTemplate.query(sql, new ColumnNameRowMapper(clazz));
        } else if (null != map && !map.isEmpty()) {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                    new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
            results = systemClass ? namedParameterJdbcTemplate.queryForList(sql, map, clazz) :
                    namedParameterJdbcTemplate.query(sql, map, new ColumnNameRowMapper(clazz));
        } else if (CollUtil.isNotEmpty(params)) {
            results = systemClass ? jdbcTemplate.queryForList(sql, clazz, params) :
                    jdbcTemplate.query(sql, new ColumnNameRowMapper(clazz), params);
        }
        return results;
    }


}
