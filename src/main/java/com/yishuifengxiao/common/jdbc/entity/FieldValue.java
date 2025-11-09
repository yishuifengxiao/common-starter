package com.yishuifengxiao.common.jdbc.entity;

import com.yishuifengxiao.common.tool.lang.TextUtil;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.SQLType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 包含属性值的POJO类属性提取对象
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/3 11:19
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FieldValue implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3388630521659133444L;

    /**
     * Java类型到SQL类型的映射
     */
    private static final Map<Class<?>, SQLType> JAVA_TO_SQL_TYPE_MAP = new HashMap<>();

    static {
        // 基本类型映射
        JAVA_TO_SQL_TYPE_MAP.put(int.class, JDBCType.INTEGER);
        JAVA_TO_SQL_TYPE_MAP.put(Integer.class, JDBCType.INTEGER);
        JAVA_TO_SQL_TYPE_MAP.put(long.class, JDBCType.BIGINT);
        JAVA_TO_SQL_TYPE_MAP.put(Long.class, JDBCType.BIGINT);
        JAVA_TO_SQL_TYPE_MAP.put(short.class, JDBCType.SMALLINT);
        JAVA_TO_SQL_TYPE_MAP.put(Short.class, JDBCType.SMALLINT);
        JAVA_TO_SQL_TYPE_MAP.put(byte.class, JDBCType.TINYINT);
        JAVA_TO_SQL_TYPE_MAP.put(Byte.class, JDBCType.TINYINT);
        JAVA_TO_SQL_TYPE_MAP.put(float.class, JDBCType.REAL);
        JAVA_TO_SQL_TYPE_MAP.put(Float.class, JDBCType.REAL);
        JAVA_TO_SQL_TYPE_MAP.put(double.class, JDBCType.DOUBLE);
        JAVA_TO_SQL_TYPE_MAP.put(Double.class, JDBCType.DOUBLE);
        JAVA_TO_SQL_TYPE_MAP.put(boolean.class, JDBCType.BOOLEAN);
        JAVA_TO_SQL_TYPE_MAP.put(Boolean.class, JDBCType.BOOLEAN);
        JAVA_TO_SQL_TYPE_MAP.put(char.class, JDBCType.CHAR);
        JAVA_TO_SQL_TYPE_MAP.put(Character.class, JDBCType.CHAR);

        // 字符串类型
        JAVA_TO_SQL_TYPE_MAP.put(String.class, JDBCType.VARCHAR);

        // 日期时间类型
        JAVA_TO_SQL_TYPE_MAP.put(Date.class, JDBCType.TIMESTAMP);
        JAVA_TO_SQL_TYPE_MAP.put(java.sql.Date.class, JDBCType.DATE);
        JAVA_TO_SQL_TYPE_MAP.put(java.sql.Time.class, JDBCType.TIME);
        JAVA_TO_SQL_TYPE_MAP.put(java.sql.Timestamp.class, JDBCType.TIMESTAMP);
        JAVA_TO_SQL_TYPE_MAP.put(LocalDate.class, JDBCType.DATE);
        JAVA_TO_SQL_TYPE_MAP.put(LocalTime.class, JDBCType.TIME);
        JAVA_TO_SQL_TYPE_MAP.put(LocalDateTime.class, JDBCType.TIMESTAMP);

        // 数值类型
        JAVA_TO_SQL_TYPE_MAP.put(java.math.BigDecimal.class, JDBCType.DECIMAL);
        JAVA_TO_SQL_TYPE_MAP.put(java.math.BigInteger.class, JDBCType.BIGINT);

        // 二进制类型
        JAVA_TO_SQL_TYPE_MAP.put(byte[].class, JDBCType.VARBINARY);
        JAVA_TO_SQL_TYPE_MAP.put(Byte[].class, JDBCType.VARBINARY);
    }

    /**
     * 使用正则表达式提取第一个空格之前的字母部分
     */
    private final static Pattern pattern = Pattern.compile("^[A-Za-z]+");

    /**
     * SQLType
     */
    private SQLType sqlType;

    /**
     * 对应的属性
     */
    private Field field;

    /**
     * POJO类中的属性在数据库中对应的名字
     */
    private Column column;

    /**
     * 是否为主键类型
     */
    private boolean primary;
    /**
     * 属性值
     */
    private Object value;

    /**
     * 构造函数属性在数据库中对应的名字
     */
    private String columnName;

    /**
     * 使用指定字段和主键标志构造FieldValue对象
     *
     * @param field   要处理的Java字段对象
     * @param primary 指示该字段是否为主键的标志
     */
    public FieldValue(Field field, boolean primary) {
        this(field, primary, null);
    }

    /**
     * 使用指定字段、主键标志和属性值构造FieldValue对象
     *
     * @param field   要处理的Java字段对象
     * @param primary 指示该字段是否为主键的标志
     * @param value   字段对应的属性值
     */
    public FieldValue(Field field, boolean primary, Object value) {
        // 初始化基本属性
        this.field = field;
        this.primary = primary;
        this.value = value;

        // 从字段上获取@Column注解
        this.column = AnnotationUtils.findAnnotation(this.field, Column.class);

        // 设置数据库列名：优先使用@Column注解中指定的name，其次使用字段名的下划线格式
        if (null != this.column && StringUtils.isNotBlank(this.column.name())) {
            this.columnName = this.column.name();
        } else if (null != this.field) {
            this.columnName = TextUtil.underscoreName(this.field.getName());
        }

        // 自动确定SQL类型
        this.sqlType = determineSqlType();
    }

    /**
     * 获取SQL类型
     *
     * @return SQL类型
     */
    public SQLType sqlType() {
        return this.sqlType;
    }

    /**
     * 根据字段类型和@Column注解确定SQL类型
     *
     * @return 对应的SQL类型
     */
    private SQLType determineSqlType() {
        // 优先从@Column注解的columnDefinition中提取类型信息
        if (this.column != null && StringUtils.isNotBlank(this.column.columnDefinition())) {
            SQLType typeFromDefinition = extractSqlTypeFromDefinition(this.column.columnDefinition());
            if (typeFromDefinition != null) {
                return typeFromDefinition;
            }
        }

        // 根据字段类型确定SQL类型
        if (this.field != null) {
            Class<?> fieldType = this.field.getType();

            // 处理数组类型
            if (fieldType.isArray()) {
                Class<?> componentType = fieldType.getComponentType();
                if (componentType == byte.class || componentType == Byte.class) {
                    return JDBCType.VARBINARY;
                }
                // 其他数组类型默认为VARCHAR
                return JDBCType.VARCHAR;
            }

            // 从映射表中获取对应的SQL类型
            SQLType mappedType = JAVA_TO_SQL_TYPE_MAP.get(fieldType);
            if (mappedType != null) {
                return mappedType;
            }

            // 对于枚举类型，使用VARCHAR
            if (fieldType.isEnum()) {
                return JDBCType.VARCHAR;
            }
        }

        // 默认使用VARCHAR类型
        return JDBCType.VARCHAR;
    }

    /**
     * 从@Column注解的columnDefinition中提取SQL类型
     *
     * @param columnDefinition 列定义字符串
     * @return 对应的SQL类型，如果无法识别则返回null
     */
    private SQLType extractSqlTypeFromDefinition(String columnDefinition) {
        if (StringUtils.isBlank(columnDefinition)) {
            return null;
        }

        String definition = columnDefinition.trim().toUpperCase();
        String dataType = extractDataType(definition);

        // 新增有效性检查防止NPE
        if (StringUtils.isBlank(dataType)) {
            return null;
        }

        // 初始化一次即可的映射表
        return TYPE_MAP.getOrDefault(dataType, null);
    }

    // 使用静态Map替代switch-case，提升性能和可维护性
    private static final Map<String, SQLType> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("INT", JDBCType.INTEGER);
        TYPE_MAP.put("INTEGER", JDBCType.INTEGER);
        TYPE_MAP.put("BIGINT", JDBCType.BIGINT);
        TYPE_MAP.put("SMALLINT", JDBCType.SMALLINT);
        TYPE_MAP.put("TINYINT", JDBCType.TINYINT);
        TYPE_MAP.put("DECIMAL", JDBCType.DECIMAL);
        TYPE_MAP.put("NUMERIC", JDBCType.DECIMAL);
        TYPE_MAP.put("FLOAT", JDBCType.FLOAT);
        TYPE_MAP.put("DOUBLE", JDBCType.DOUBLE);
        TYPE_MAP.put("REAL", JDBCType.REAL);
        TYPE_MAP.put("BOOLEAN", JDBCType.BOOLEAN);
        TYPE_MAP.put("BOOL", JDBCType.BOOLEAN);
        TYPE_MAP.put("CHAR", JDBCType.CHAR);
        TYPE_MAP.put("VAR", JDBCType.VARCHAR);
        TYPE_MAP.put("VARCHAR", JDBCType.VARCHAR);
        TYPE_MAP.put("TEXT", JDBCType.CLOB);
        TYPE_MAP.put("CLOB", JDBCType.CLOB);
        TYPE_MAP.put("BLOB", JDBCType.BLOB);
        TYPE_MAP.put("BINARY", JDBCType.BINARY);
        TYPE_MAP.put("VARBINARY", JDBCType.VARBINARY);
        TYPE_MAP.put("DATE", JDBCType.DATE);
        TYPE_MAP.put("TIME", JDBCType.TIME);
        TYPE_MAP.put("TIMESTAMP", JDBCType.TIMESTAMP);
        TYPE_MAP.put("DATETIME", JDBCType.TIMESTAMP);
    }


    /**
     * 从SQL列定义中提取数据类型（截取第一个空格前的英文字母部分）
     * 例如："BIGINT(20) NOT NULL AUTO_INCREMENT" -> "BIGINT"
     * "VARCHAR(255) NOT NULL COMMENT '用户名'" -> "VARCHAR"
     * "DATETIME NULL COMMENT 'datetime格式时间'" -> "DATETIME"
     *
     * @param sqlDefinition SQL列定义字符串
     * @return 数据类型字符串，如果无法提取则返回空字符串
     */
    public static String extractDataType(String sqlDefinition) {
        if (sqlDefinition == null || sqlDefinition.trim().isEmpty()) {
            return "";
        }

        String trimmed = sqlDefinition.trim();

        // 先查找左括号
        int parenthesisIndex = trimmed.indexOf('(');
        if (parenthesisIndex > 0) {
            return trimmed.substring(0, parenthesisIndex);
        }

        // 如果没有括号，再查找空格
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex > 0) {
            return trimmed.substring(0, spaceIndex);
        }

        // 如果没有括号和空格，直接返回整个字符串
        return trimmed;
    }

    /**
     * 设置属性值并返回当前对象（支持链式调用）
     *
     * @param value 要设置的属性值
     * @return 当前FieldValue对象实例，便于链式调用
     */
    public FieldValue setValue(Object value) {
        this.value = value;
        return this;
    }

    /**
     * 判断当前对象的value属性是否不为null
     *
     * @return true表示value不为null，false表示value为null
     */
    public boolean isNotNullVal() {
        return null != this.value;
    }

    /**
     * 判断当前对象的值是否为null
     *
     * @return true表示值为null，false表示值不为null
     */
    public boolean isNullVal() {
        return null == this.value;
    }

    @Override
    public String toString() {
        return "FieldValue{" + "sqlType=" + sqlType + ", value=" + value + ", primary=" + primary + ", columnName='" + columnName + '\'' + '}';
    }
}
