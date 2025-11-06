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

        // 常见数据库类型映射
        if (definition.contains("INT") || definition.contains("INTEGER")) {
            return JDBCType.INTEGER;
        } else if (definition.contains("BIGINT")) {
            return JDBCType.BIGINT;
        } else if (definition.contains("SMALLINT")) {
            return JDBCType.SMALLINT;
        } else if (definition.contains("TINYINT")) {
            return JDBCType.TINYINT;
        } else if (definition.contains("DECIMAL") || definition.contains("NUMERIC")) {
            return JDBCType.DECIMAL;
        } else if (definition.contains("FLOAT")) {
            return JDBCType.FLOAT;
        } else if (definition.contains("DOUBLE")) {
            return JDBCType.DOUBLE;
        } else if (definition.contains("REAL")) {
            return JDBCType.REAL;
        } else if (definition.contains("BOOLEAN") || definition.contains("BOOL")) {
            return JDBCType.BOOLEAN;
        } else if (definition.contains("CHAR") && !definition.contains("VAR")) {
            return JDBCType.CHAR;
        } else if (definition.contains("VARCHAR")) {
            return JDBCType.VARCHAR;
        } else if (definition.contains("TEXT") || definition.contains("CLOB")) {
            return JDBCType.CLOB;
        } else if (definition.contains("BLOB")) {
            return JDBCType.BLOB;
        } else if (definition.contains("BINARY")) {
            return JDBCType.BINARY;
        } else if (definition.contains("VARBINARY")) {
            return JDBCType.VARBINARY;
        } else if (definition.contains("DATE")) {
            return JDBCType.DATE;
        } else if (definition.contains("TIME")) {
            return JDBCType.TIME;
        } else if (definition.contains("TIMESTAMP") || definition.contains("DATETIME")) {
            return JDBCType.TIMESTAMP;
        }

        return null;
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
        return "FieldValue{" + "value=" + value + '}';
    }
}
