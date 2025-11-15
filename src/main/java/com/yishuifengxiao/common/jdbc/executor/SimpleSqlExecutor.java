package com.yishuifengxiao.common.jdbc.executor;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.util.FieldUtils;
import com.yishuifengxiao.common.jdbc.util.SimpleRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;

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
    /**
     * 最大数据包大小（默认64MB，与MySQL默认值一致）
     */
    private static final int MAX_PACKET_SIZE = 64 * 1024 * 1024;

    /**
     * 大字段分块大小（1MB）
     */
    private static final int CHUNK_SIZE = 1024 * 1024;

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

        // 检查是否包含大字段
        if (containsLargeField(fieldValues)) {
            return updateWithLargeFields(jdbcTemplate, sql, fieldValues);
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                setPreparedStatementParameters(ps, fieldValues);
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            log.error("更新失败: ", e);
            throw e;
        }

        logSqlExecutionEnd("update", keyHolder);
        return keyHolder;
    }

    /**
     * 检查是否包含大字段
     */
    private boolean containsLargeField(List<FieldValue> fieldValues) {
        if (fieldValues == null) {
            return false;
        }

        for (FieldValue fieldValue : fieldValues) {
            if (fieldValue == null) {
                continue;
            }

            Object value = fieldValue.getValue();
            if (value != null && isLargeObject(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为大型对象
     */
    private boolean isLargeObject(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof byte[]) {
            return ((byte[]) value).length > CHUNK_SIZE;
        } else if (value instanceof String) {
            return ((String) value).length() > CHUNK_SIZE;
        } else if (value instanceof InputStream) {
            // 流对象总是被认为是大型对象
            return true;
        } else if (value instanceof Blob) {
            return true;
        } else if (value instanceof Clob) {
            return true;
        }

        return false;
    }

    /**
     * 大字段阈值（16MB，小于MySQL默认的max_allowed_packet）
     */
    private static final int LARGE_FIELD_THRESHOLD = 16 * 1024 * 1024;

    /**
     * 处理包含大字段的更新操作 - 使用原生JDBC连接
     */
    private KeyHolder updateWithLargeFields(JdbcTemplate jdbcTemplate, String sql, List<FieldValue> fieldValues) {
        //# 对大字段处理优化的配置
        //spring.datasource.url=jdbc:mysql://localhost:3306/yourdb?useServerPrepStmts=false&cachePrepStmts=false&rewriteBatchedStatements=true&useSSL=false&allowPublicKeyRetrieval=true
        //useServerPrepStmts=false ⭐ 关键参数
        //作用：禁用服务器端预处理语句，使用客户端预处理
        //效果：对于大字段，客户端预处理可以减少数据包大小，因为参数值在客户端序列化
        //推荐值：false（对大字段处理更友好）
        //2. cachePrepStmts=false ⭐ 相关参数
        //作用：禁用预处理语句缓存
        //效果：与useServerPrepStmts=false配合使用，避免缓存大字段语句
        //推荐值：false
        //3. rewriteBatchedStatements=true
        //作用：重写批量语句，将多个INSERT合并为一个
        //效果：主要针对批量操作，对单条大记录插入影响有限
        //推荐值：true（但当前场景作用不大）
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        // 使用原生JDBC连接，避免Spring JDBC模板的限制
        jdbcTemplate.execute((ConnectionCallback<Object>) connection -> {
            // 禁用自动提交，确保事务一致性
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // 设置参数，使用流式处理大字段
                setStreamingParameters(ps, fieldValues);

                // 执行更新
                int affectedRows = ps.executeUpdate();
                log.debug("包含大字段的更新操作影响行数: {}", affectedRows);

                // 获取生成的主键
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        keyHolder.getKeyList().add(Map.of("GENERATED_KEY", rs.getObject(1)));
                    }
                }

                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw new DataAccessException("包含大字段的更新失败", e) {
                };
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        log.warn("关闭PreparedStatement失败", e);
                    }
                }
                connection.setAutoCommit(originalAutoCommit);
            }
        });

        return keyHolder;
    }

    /**
     * 流式设置参数
     */
    private void setStreamingParameters(PreparedStatement ps, List<FieldValue> fieldValues) throws SQLException {
        int parameterIndex = 1;

        for (FieldValue fieldValue : fieldValues) {
            if (fieldValue == null) {
                ps.setNull(parameterIndex, Types.NULL);
                parameterIndex++;
                continue;
            }

            Object value = fieldValue.getValue();
            SQLType sqlType = fieldValue.sqlType();

            if (value != null && value instanceof Collection) {
                // 处理IN查询参数（集合类型）
                Collection<?> collection = (Collection<?>) value;
                for (Object item : collection) {
                    setStreamingParameter(ps, parameterIndex, item, sqlType);
                    parameterIndex++;
                }
            } else {
                // 处理单个参数
                setStreamingParameter(ps, parameterIndex, value, sqlType);
                parameterIndex++;
            }
        }
    }

    /**
     * 流式设置单个参数
     */
    private void setStreamingParameter(PreparedStatement ps, int parameterIndex, Object value, SQLType sqlType)
            throws SQLException {
        if (value == null) {
            if (sqlType != null) {
                ps.setNull(parameterIndex, sqlType.getVendorTypeNumber());
            } else {
                ps.setNull(parameterIndex, Types.NULL);
            }
            return;
        }

        // 对大字段进行流式处理
        if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            if (bytes.length > LARGE_FIELD_THRESHOLD) {
                // 使用流式设置大字节数组
                ps.setBinaryStream(parameterIndex, new ByteArrayInputStream(bytes), bytes.length);
            } else {
                ps.setBytes(parameterIndex, bytes);
            }
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.length() > LARGE_FIELD_THRESHOLD) {
                // 使用流式设置大字符串
                ps.setCharacterStream(parameterIndex, new StringReader(str), str.length());
            } else {
                ps.setString(parameterIndex, str);
            }
        } else if (value instanceof InputStream) {
            // 输入流直接使用流式设置
            ps.setBinaryStream(parameterIndex, (InputStream) value);
        } else if (value instanceof Blob) {
            ps.setBlob(parameterIndex, (Blob) value);
        } else if (value instanceof Clob) {
            ps.setClob(parameterIndex, (Clob) value);
        } else {
            // 普通参数正常设置
            if (sqlType == null || JDBCType.NULL.equals(sqlType) || JDBCType.OTHER.equals(sqlType)) {
                ps.setObject(parameterIndex, value);
            } else {
                ps.setObject(parameterIndex, value, sqlType);
            }
        }
    }

    /**
     * 设置包含大字段的参数
     */
    private void setPreparedStatementParametersWithLargeFields(PreparedStatement ps, List<FieldValue> fieldValues)
            throws SQLException {
        int parameterIndex = 1;

        for (FieldValue fieldValue : fieldValues) {
            if (fieldValue == null) {
                continue;
            }

            Object value = fieldValue.getValue();
            SQLType sqlType = fieldValue.sqlType();

            if (value != null && value instanceof Collection) {
                // 处理IN查询参数（集合类型）
                Collection<?> collection = (Collection<?>) value;
                for (Object item : collection) {
                    Object processedValue = processParameterValue(item, sqlType);
                    setLargeObjectParameter(ps, parameterIndex, processedValue, sqlType);
                    parameterIndex++;
                }
            } else {
                // 处理单个参数
                Object processedValue = processParameterValue(value, sqlType);
                setLargeObjectParameter(ps, parameterIndex, processedValue, sqlType);
                parameterIndex++;
            }
        }
    }

    /**
     * 设置大对象参数
     */
    private void setLargeObjectParameter(PreparedStatement ps, int parameterIndex, Object value, SQLType sqlType)
            throws SQLException {
        if (value == null) {
            if (sqlType != null) {
                ps.setNull(parameterIndex, sqlType.getVendorTypeNumber());
            } else {
                ps.setNull(parameterIndex, Types.NULL);
            }
            return;
        }

        // 对大字段进行特殊处理
        if (value instanceof byte[] && ((byte[]) value).length > CHUNK_SIZE) {
            // 大字节数组使用setBytes，让JDBC驱动自动处理
            ps.setBytes(parameterIndex, (byte[]) value);
        } else if (value instanceof String && ((String) value).length() > CHUNK_SIZE) {
            // 大字符串使用setCharacterStream
            String stringValue = (String) value;
            ps.setCharacterStream(parameterIndex, new StringReader(stringValue), stringValue.length());
        } else if (value instanceof InputStream) {
            // 输入流使用setBinaryStream
            ps.setBinaryStream(parameterIndex, (InputStream) value);
        } else if (value instanceof Blob) {
            ps.setBlob(parameterIndex, (Blob) value);
        } else if (value instanceof Clob) {
            ps.setClob(parameterIndex, (Clob) value);
        } else {
            // 普通参数正常设置
            if (sqlType == null || JDBCType.NULL.equals(sqlType) || JDBCType.OTHER.equals(sqlType)) {
                ps.setObject(parameterIndex, value);
            } else {
                ps.setObject(parameterIndex, value, sqlType);
            }
        }
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