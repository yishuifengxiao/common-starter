package com.yishuifengxiao.common.jdbc.executor;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.util.FieldUtils;
import com.yishuifengxiao.common.jdbc.util.SimpleRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 系统语句执行器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleSqlExecutor implements SqlExecutor {


    /**
     * 日志前缀常量
     */
    private static final String LOG_PREFIX = "【yishuifengxiao-common-spring-boot-starter】";

    /**
     * 默认批量大小
     */
    private static final int DEFAULT_BATCH_SIZE = 500;


    private ZoneId timeZone;

    public SimpleSqlExecutor(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public SimpleSqlExecutor() {
    }

    /**
     * 执行非查询语句
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param args         最终执行的sql语句对应的参数
     * @return 受影响的记录的数量
     */
    @Override
    public int execute(JdbcTemplate jdbcTemplate, String sql, FieldValue... args) {
        logSqlExecutionStart("执行sql", sql, args);

        int count;
        if (args == null || args.length == 0) {
            // 无参数的情况
            count = jdbcTemplate.update(sql);
        } else {
            // 有参数的情况 - 使用PreparedStatementCreator确保参数设置正确
            count = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                setPreparedStatementParameters(ps, Arrays.asList(args));
                return ps;
            });
        }

        logSqlExecutionEnd("执行sql", count);

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
    @Override
    public <T> List<T> findAll(JdbcTemplate jdbcTemplate, Class<T> clazz, String sql, FieldValue... args) {
        logSqlExecutionStart("查询记录", sql, args);
        List<T> results = null;

        if (null == args || args.length == 0) {
            if (FieldUtils.isBasicResult(clazz)) {
                results = jdbcTemplate.queryForList(sql, clazz);
            } else {
                results = jdbcTemplate.query(sql, new SimpleRowMapper<>(clazz, this.timeZone));
            }
        } else {
            // 有参数的情况 - 使用setPreparedStatementParameters方法
            if (FieldUtils.isBasicResult(clazz)) {
                // 对于基本类型，使用queryForList方法
                Object[] values = new Object[args.length];
                int[] argTypes = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    // 处理日期时间类型的时区转换
                    Object value = processDateTimeValue(args[i].getValue());
                    // 处理原始数据类型转换
                    value = processPrimitiveValue(value);
                    values[i] = value;
                    // 获取SQL类型
                    SQLType sqlType = args[i].sqlType();
                    argTypes[i] = sqlType != null ? sqlType.getVendorTypeNumber() : Types.OTHER;
                }
                results = jdbcTemplate.queryForList(sql, values, argTypes, clazz);
            } else {
                // 对于对象类型，使用PreparedStatementCreator
                results = jdbcTemplate.query(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    setPreparedStatementParameters(ps, Arrays.asList(args));
                    return ps;
                }, new SimpleRowMapper<>(clazz, this.timeZone));
            }
        }

        logSqlExecutionEnd("查询记录", results.size());
        return results;
    }

    /**
     * 执行sql并返回数据主键
     *
     * @param jdbcTemplate JdbcTemplate
     * @param sql          最终执行的sql语句
     * @param fieldValues  最终执行的sql语句对应的参数
     * @return 数据主键持有者
     */
    @Override
    public KeyHolder update(JdbcTemplate jdbcTemplate, String sql, List<FieldValue> fieldValues) {

        logSqlExecutionStart("查询记录", sql, fieldValues);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            // 将参数转换为Object数组
            List<FieldValue> parms = fieldValues.stream()
                    .map(fieldValue -> {
                        Object value = processParameterValue(fieldValue.getValue(), fieldValue.sqlType());
                        fieldValue.setValue(value);
                        return fieldValue;
                    })
                    .collect(Collectors.toList());

            int updateCount = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                // 设置参数
                setPreparedStatementParameters(ps, parms);
                return ps;
            }, keyHolder);

            log.debug("{}SQL执行完成，影响行数: {}, 主键值: {}", LOG_PREFIX, updateCount, keyHolder);
        } catch (DataAccessException ex) {
            log.error("{}SQL执行失败", LOG_PREFIX, ex);
            throw ex;
        }

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
    public void batchUpdate(JdbcTemplate jdbcTemplate, String sql, int[] types, List<List<FieldValue>> parameters) {

        log.trace("{}\n{}  (批量执行sql) ============= start =================\n{}   (批量执行sql) 执行的sql语句为 {}", LOG_PREFIX, LOG_PREFIX, LOG_PREFIX, sql);

        try {
            // 使用JdbcTemplate的batchUpdate方法处理批量操作
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    List<FieldValue> fieldValues = parameters.get(i);
                    setPreparedStatementParameters(ps, fieldValues);
                    if (Objects.equals(i, parameters.size())) {
                        ps.executeLargeBatch();
                        ps.clearBatch();
                    } else {
                        if (i % DEFAULT_BATCH_SIZE == 0) {
                            ps.executeLargeBatch();
                            ps.clearBatch();
                        }
                    }
                }

                @Override
                public int getBatchSize() {
                    return parameters.size();
                }
            });

        } catch (DataAccessException e) {
            log.error("批量更新失败: ", e);
            throw e;
        }

        log.trace("{}  (批量执行sql) ============= end =================\n{}\n", LOG_PREFIX, LOG_PREFIX);
    }


    /**
     * 记录SQL执行开始日志
     */
    private void logSqlExecutionStart(String operation, String sql, Object args) {
        if (!log.isTraceEnabled()) {
            return;
        }
        log.trace("\r\n{}", LOG_PREFIX);
        log.trace("{}  ({}) ============= start ================= ", LOG_PREFIX, operation);
        log.trace("{}   ({}) 执行的sql语句为 {}", LOG_PREFIX, operation, sql);
        log.trace("{}  ({}) 执行的sql语句参数数量为 {} ,参数值为 {}", LOG_PREFIX, operation, StringUtils.countMatches(sql, "?"), args);
    }

    /**
     * 记录SQL执行结束日志
     */
    private void logSqlExecutionEnd(String operation, Object result) {
        if (!log.isTraceEnabled()) {
            return;
        }
        log.trace("{}  ({}) 执行的sql语句对应的结果为 {}", LOG_PREFIX, operation, result);
        log.trace("{}  ({}) ============= end ================= ", LOG_PREFIX, operation);
        log.trace("{} \r\n", LOG_PREFIX);
    }


    /**
     * 设置PreparedStatement参数
     */
    private void setPreparedStatementParameters(PreparedStatement ps, List<FieldValue> fieldValues) throws
            SQLException {
        int parameterIndex = 1; // 参数索引从1开始

        for (FieldValue fieldValue : fieldValues) {
            if (fieldValue == null) {
                continue;
            }

            Object value = fieldValue.getValue();
            SQLType sqlType = fieldValue.sqlType();

            // 处理IN查询参数（集合类型）
            if (value != null && value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                for (Object item : collection) {
                    Object processedValue = processParameterValue(item, sqlType);
                    if (sqlType == null || JDBCType.NULL.equals(sqlType) || JDBCType.OTHER.equals(sqlType)) {
                        ps.setObject(parameterIndex, processedValue);
                    } else {
                        ps.setObject(parameterIndex, processedValue, sqlType);
                    }
                    parameterIndex++;
                }
            } else {
                // 处理单个参数
                Object processedValue = processParameterValue(value, sqlType);
                if (processedValue != null) {
                    if (sqlType == null || JDBCType.NULL.equals(sqlType) || JDBCType.OTHER.equals(sqlType)) {
                        ps.setObject(parameterIndex, processedValue);
                    } else {
                        ps.setObject(parameterIndex, processedValue, sqlType);
                    }
                } else {
                    if (sqlType != null) {
                        ps.setNull(parameterIndex, sqlType.getVendorTypeNumber());
                    } else {
                        ps.setNull(parameterIndex, Types.NULL);
                    }
                }
                parameterIndex++;
            }
        }
    }

    /**
     * 处理参数值（包含时区转换和类型转换）
     */
    private Object processParameterValue(Object value, SQLType sqlType) {
        if (value == null) {
            return null;
        }

        // 处理日期时间类型的时区转换
        Object processedValue = processDateTimeValue(value);

        // 处理原始数据类型转换
        processedValue = processPrimitiveValue(processedValue);

        return processedValue;
    }

    /**
     * 处理日期时间类型的时区转换
     */
    private Object processDateTimeValue(Object value) {
        if (value == null) {
            return null;
        }

        // 如果配置了应用时区，进行时区转换
        if (this.timeZone != null) {
            if (value instanceof ZonedDateTime) {
                return ((ZonedDateTime) value).withZoneSameInstant(this.timeZone);
            } else if (value instanceof OffsetDateTime) {
                return ((OffsetDateTime) value).atZoneSameInstant(this.timeZone).toOffsetDateTime();
            } else if (value instanceof LocalDateTime) {
                // LocalDateTime没有时区信息，直接返回
                return value;
            } else if (value instanceof Instant) {
                return ((Instant) value).atZone(this.timeZone).toLocalDateTime();
            } else if (value instanceof Date) {
                return ((Date) value).toInstant().atZone(this.timeZone).toLocalDateTime();
            }
        }

        return value;
    }

    /**
     * 处理原始数据类型转换
     */
    private Object processPrimitiveValue(Object value) {
        if (value == null) {
            return null;
        }

        // 如果是原始类型，转换为包装类型
        if (value.getClass().isPrimitive()) {
            return convertPrimitiveToWrapper(value);
        }

        return value;
    }

    /**
     * 将原始类型转换为包装类型
     */
    private Object convertPrimitiveToWrapper(Object primitive) {
        if (primitive instanceof Integer) {
            return (Integer) primitive;
        } else if (primitive instanceof Long) {
            return (Long) primitive;
        } else if (primitive instanceof Double) {
            return (Double) primitive;
        } else if (primitive instanceof Float) {
            return (Float) primitive;
        } else if (primitive instanceof Boolean) {
            return (Boolean) primitive;
        } else if (primitive instanceof Byte) {
            return (Byte) primitive;
        } else if (primitive instanceof Short) {
            return (Short) primitive;
        } else if (primitive instanceof Character) {
            return (Character) primitive;
        }

        return primitive;
    }


}
