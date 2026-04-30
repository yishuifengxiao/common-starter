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
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

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
                // 对于基本类型,使用queryForList方法
                Object[] values = new Object[args.length];
                int[] argTypes = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    // 处理日期时间类型的时区转换(已返回 java.sql 类型)
                    Object value = processDateTimeValue(args[i].getValue());
                    values[i] = value;
                    // 获取SQL类型
                    SQLType sqlType = args[i].sqlType();
                    argTypes[i] = sqlType != null ? sqlType.getVendorTypeNumber() : Types.OTHER;
                }
                results = jdbcTemplate.queryForList(sql, values, argTypes, clazz);
            } else {
                // 对于对象类型,使用PreparedStatementCreator
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
        // # 对大字段处理优化的配置
        // spring.datasource.url=jdbc:mysql://localhost:3306/yourdb?useServerPrepStmts=false&cachePrepStmts=false&rewriteBatchedStatements=true&useSSL=false&allowPublicKeyRetrieval=true
        // useServerPrepStmts=false ⭐ 关键参数
        // 作用：禁用服务器端预处理语句，使用客户端预处理
        // 效果：对于大字段，客户端预处理可以减少数据包大小，因为参数值在客户端序列化
        // 推荐值：false（对大字段处理更友好）
        // 2. cachePrepStmts=false ⭐ 相关参数
        // 作用：禁用预处理语句缓存
        // 效果：与useServerPrepStmts=false配合使用，避免缓存大字段语句
        // 推荐值：false
        // 3. rewriteBatchedStatements=true
        // 作用：重写批量语句，将多个INSERT合并为一个
        // 效果：主要针对批量操作，对单条大记录插入影响有限
        // 推荐值：true（但当前场景作用不大）
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

        log.trace("{}\n{}  (批量执行sql) ============= start =================\n{}   (批量执行sql) 执行的sql语句为 {}", LOG_PREFIX,
                LOG_PREFIX, LOG_PREFIX, sql);

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
        log.trace("{}  ({}) 执行的sql语句参数数量为 {} ,参数值为 {}", LOG_PREFIX, operation, StringUtils.countMatches(sql, "?"),
                args);
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
    private void setPreparedStatementParameters(PreparedStatement ps, List<FieldValue> fieldValues)
            throws SQLException {
        if (ps == null) {
            throw new SQLException("PreparedStatement不能为null");
        }
        if (fieldValues == null) {
            return;
        }

        int parameterIndex = 1;

        for (FieldValue fieldValue : fieldValues) {
            if (fieldValue == null) {
                continue;
            }

            Object value = fieldValue.getValue();
            SQLType sqlType = fieldValue.sqlType();

            if (value != null && value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                for (Object item : collection) {
                    Object processedValue = processParameterValue(item, sqlType);
                    setObjectParameter(ps, parameterIndex, processedValue, sqlType);
                    parameterIndex++;
                }
            } else {
                Object processedValue = processParameterValue(value, sqlType);
                setObjectParameter(ps, parameterIndex, processedValue, sqlType);
                parameterIndex++;
            }
        }
    }

    /**
     * 设置对象参数到PreparedStatement
     */
    private void setObjectParameter(PreparedStatement ps, int parameterIndex, Object value, SQLType sqlType)
            throws SQLException {
        if (value == null) {
            if (sqlType != null && !JDBCType.NULL.equals(sqlType) && !JDBCType.OTHER.equals(sqlType)) {
                ps.setNull(parameterIndex, sqlType.getVendorTypeNumber());
            } else {
                ps.setNull(parameterIndex, Types.NULL);
            }
        } else {
            if (sqlType == null || JDBCType.NULL.equals(sqlType) || JDBCType.OTHER.equals(sqlType)) {
                ps.setObject(parameterIndex, value);
            } else {
                ps.setObject(parameterIndex, value, sqlType);
            }
        }
    }

    /**
     * 处理参数值(包含时区转换和类型转换)
     */
    private Object processParameterValue(Object value, SQLType sqlType) {
        if (value == null) {
            return null;
        }

        // 处理日期时间类型的时区转换(已包含转换为 java.sql 类型)
        Object processedValue = processDateTimeValue(value);

        // 不再需要 processPrimitiveValue,因为 java.sql 类型已经是包装类型
        return processedValue;
    }

    /**
     * 处理日期时间类型的时区转换
     * <p>
     * 【重要】时区转换原则:
     * 1. 应用层使用带时区的时间类型(ZonedDateTime/OffsetDateTime/Instant)
     * 2. 或者使用不带时区但隐含应用时区的时间类型(LocalDateTime/Date)
     * 3. 数据库存储不带时区的本地时间(基于数据库时区)
     * 4. 写入数据库时:应用时区 -> 数据库时区 -> 本地时间 -> java.sql类型
     * 
     * 【特别注意】对于 LocalDate 和 LocalTime 类型,不进行日期/时间的时区转换,
     * 因为它们代表的是"日期"和"时间"概念,而非"时间点"。
     * 例如:生日、营业时间等不应随时区变化而变化。
     *
     * @param value 原始值
     * @return 转换后的 java.sql 类型值
     */
    private Object processDateTimeValue(Object value) {
        if (value == null) {
            return null;
        }

        // 如果没有配置数据库时区,直接转换为 java.sql 类型
        if (this.timeZone == null) {
            return convertToJavaSqlType(value);
        }

        ZoneId appZone = ZoneId.systemDefault();

        // 如果应用时区与数据库时区相同,无需转换
        if (isSameTimeZone(appZone, this.timeZone)) {
            return convertToJavaSqlType(value);
        }

        try {
            if (value instanceof java.util.Date) {
                // java.util.Date 转换为数据库时区的 LocalDateTime,然后转换为 java.sql.Timestamp
                java.util.Date date = (java.util.Date) value;
                java.time.Instant instant = date.toInstant();
                java.time.ZonedDateTime appTime = instant.atZone(appZone);
                java.time.ZonedDateTime dbTime = appTime.withZoneSameInstant(this.timeZone);
                return java.sql.Timestamp.valueOf(dbTime.toLocalDateTime());

            } else if (value instanceof java.time.LocalDateTime) {
                // LocalDateTime 转换为数据库时区的 LocalDateTime,然后转换为 java.sql.Timestamp
                java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) value;
                java.time.ZonedDateTime appTime = localDateTime.atZone(appZone);
                java.time.ZonedDateTime dbTime = appTime.withZoneSameInstant(this.timeZone);
                return java.sql.Timestamp.valueOf(dbTime.toLocalDateTime());

            } else if (value instanceof java.time.ZonedDateTime) {
                // ZonedDateTime 转换为数据库时区的 LocalDateTime,然后转换为 java.sql.Timestamp
                java.time.ZonedDateTime zonedDateTime = (java.time.ZonedDateTime) value;
                java.time.ZonedDateTime dbTime = zonedDateTime.withZoneSameInstant(this.timeZone);
                return java.sql.Timestamp.valueOf(dbTime.toLocalDateTime());

            } else if (value instanceof java.time.OffsetDateTime) {
                // OffsetDateTime 转换为数据库时区的 LocalDateTime,然后转换为 java.sql.Timestamp
                java.time.OffsetDateTime offsetDateTime = (java.time.OffsetDateTime) value;
                java.time.ZonedDateTime dbTime = offsetDateTime.atZoneSameInstant(this.timeZone);
                return java.sql.Timestamp.valueOf(dbTime.toLocalDateTime());

            } else if (value instanceof java.time.Instant) {
                // Instant 转换为数据库时区的 LocalDateTime,然后转换为 java.sql.Timestamp
                java.time.Instant instant = (java.time.Instant) value;
                java.time.ZonedDateTime dbTime = instant.atZone(this.timeZone);
                return java.sql.Timestamp.valueOf(dbTime.toLocalDateTime());

            } else if (value instanceof java.time.LocalDate) {
                // LocalDate 表示日期概念,不进行跨时区转换
                java.time.LocalDate localDate = (java.time.LocalDate) value;
                return java.sql.Date.valueOf(localDate);

            } else if (value instanceof java.time.LocalTime) {
                // LocalTime 表示时间概念,不进行跨时区转换
                java.time.LocalTime localTime = (java.time.LocalTime) value;
                return java.sql.Time.valueOf(localTime);

            } else if (value instanceof java.sql.Date) {
                // java.sql.Date 已经是日期类型,不进行时区转换
                return value;

            } else if (value instanceof java.sql.Time) {
                // java.sql.Time 已经是时间类型,不进行时区转换
                return value;

            } else if (value instanceof java.sql.Timestamp) {
                // java.sql.Timestamp 需要进行时区转换
                java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
                java.time.LocalDateTime localDateTime = timestamp.toLocalDateTime();
                java.time.ZonedDateTime appTime = localDateTime.atZone(appZone);
                java.time.ZonedDateTime dbTime = appTime.withZoneSameInstant(this.timeZone);
                return java.sql.Timestamp.valueOf(dbTime.toLocalDateTime());
            }

        } catch (Exception e) {
            log.warn("{}日期时间转换失败,使用原始值: {}", LOG_PREFIX, e.getMessage());
        }

        // 对于其他类型,直接返回原值
        return value;
    }

    /**
     * 判断两个时区是否实际等效
     * <p>
     * 比较同一时刻在两个时区的偏移量是否相同，而不是简单比较ID字符串
     *
     * @param zone1 时区1
     * @param zone2 时区2
     * @return 如果两个时区在当前时刻的偏移量相同则返回true
     */
    private boolean isSameTimeZone(ZoneId zone1, ZoneId zone2) {
        if (zone1 == null || zone2 == null) {
            return false;
        }
        
        // 如果ID相同，直接返回true
        if (zone1.equals(zone2)) {
            return true;
        }
        
        // 比较同一时刻在两个时区的偏移量
        Instant now = Instant.now();
        return zone1.getRules().getOffset(now).equals(zone2.getRules().getOffset(now));
    }

    /**
     * 将 Java 8 时间类型转换为 java.sql 类型(无时区转换)
     * 
     * @param value 原始值
     * @return 转换后的 java.sql 类型值
     */
    private Object convertToJavaSqlType(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof java.time.LocalDateTime) {
            return java.sql.Timestamp.valueOf((java.time.LocalDateTime) value);
        } else if (value instanceof java.time.LocalDate) {
            return java.sql.Date.valueOf((java.time.LocalDate) value);
        } else if (value instanceof java.time.LocalTime) {
            return java.sql.Time.valueOf((java.time.LocalTime) value);
        } else if (value instanceof java.time.ZonedDateTime) {
            java.time.ZonedDateTime zonedDateTime = (java.time.ZonedDateTime) value;
            return java.sql.Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        } else if (value instanceof java.time.OffsetDateTime) {
            java.time.OffsetDateTime offsetDateTime = (java.time.OffsetDateTime) value;
            return java.sql.Timestamp.valueOf(offsetDateTime.toLocalDateTime());
        } else if (value instanceof java.time.Instant) {
            return java.sql.Timestamp.from((java.time.Instant) value);
        } else if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }

        return value;
    }

}