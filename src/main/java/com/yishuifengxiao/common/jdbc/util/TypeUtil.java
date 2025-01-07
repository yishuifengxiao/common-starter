package com.yishuifengxiao.common.jdbc.util;

import org.springframework.jdbc.support.JdbcUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * java类型转为sql类型
 */
public class TypeUtil {


    public static final SQLType TYPE_UNKNOWN = new SQLType() {
        @Override
        public String getName() {
            return "UNKNOWN";
        }

        @Override
        public String getVendor() {
            return "Spring";
        }

        @Override
        public Integer getVendorTypeNumber() {
            return JdbcUtils.TYPE_UNKNOWN;
        }

        @Override
        public String toString() {
            return getName();
        }
    };
    private static final Map<Class<?>, SQLType> sqlTypeMappings = new HashMap<>();

    static {

        sqlTypeMappings.put(String.class, JDBCType.VARCHAR);
        sqlTypeMappings.put(BigInteger.class, JDBCType.BIGINT);
        sqlTypeMappings.put(BigDecimal.class, JDBCType.DECIMAL);
        sqlTypeMappings.put(Byte.class, JDBCType.TINYINT);
        sqlTypeMappings.put(byte.class, JDBCType.TINYINT);
        sqlTypeMappings.put(Short.class, JDBCType.SMALLINT);
        sqlTypeMappings.put(short.class, JDBCType.SMALLINT);
        sqlTypeMappings.put(Integer.class, JDBCType.INTEGER);
        sqlTypeMappings.put(int.class, JDBCType.INTEGER);
        sqlTypeMappings.put(Long.class, JDBCType.BIGINT);
        sqlTypeMappings.put(long.class, JDBCType.BIGINT);
        sqlTypeMappings.put(Double.class, JDBCType.DOUBLE);
        sqlTypeMappings.put(double.class, JDBCType.DOUBLE);
        sqlTypeMappings.put(Float.class, JDBCType.REAL);
        sqlTypeMappings.put(float.class, JDBCType.REAL);
        sqlTypeMappings.put(Boolean.class, JDBCType.BIT);
        sqlTypeMappings.put(boolean.class, JDBCType.BIT);
        sqlTypeMappings.put(byte[].class, JDBCType.VARBINARY);
        sqlTypeMappings.put(Date.class, JDBCType.DATE);
        sqlTypeMappings.put(Time.class, JDBCType.TIME);
        sqlTypeMappings.put(Timestamp.class, JDBCType.TIMESTAMP);
        sqlTypeMappings.put(OffsetDateTime.class, JDBCType.TIMESTAMP_WITH_TIMEZONE);
        sqlTypeMappings.put(java.util.Date.class, JDBCType.TIMESTAMP);
        sqlTypeMappings.put(LocalDateTime.class, JDBCType.TIMESTAMP);
        sqlTypeMappings.put(Instant.class, JDBCType.TIMESTAMP);
        sqlTypeMappings.put(LocalDate.class, JDBCType.DATE);
        sqlTypeMappings.put(LocalTime.class, JDBCType.TIME);
        sqlTypeMappings.put(BigInteger.class, JDBCType.BIGINT);
        sqlTypeMappings.put(BigDecimal.class, JDBCType.NUMERIC);
    }


    /**
     * Returns the {@link SQLType} value suitable for passing a value of the provided type to
     * JDBC driver.
     *
     * @param type The type of value to be bound to a {@link java.sql.PreparedStatement}.
     * @return a matching {@link SQLType} or {@link #TYPE_UNKNOWN}.
     */
    public static SQLType targetSqlTypeFor(Class<?> type) {
        if (null == type) {
            return JDBCType.VARCHAR;
        }

        return sqlTypeMappings.keySet().stream() //
                .filter(k -> k.isAssignableFrom(type)) //
                .findFirst() //
                .map(sqlTypeMappings::get) //
                .orElse(TypeUtil.TYPE_UNKNOWN);
    }
}
