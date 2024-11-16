package com.yishuifengxiao.common.jdbc.entity;

import com.yishuifengxiao.common.jdbc.util.FieldUtils;
import com.yishuifengxiao.common.jdbc.util.TypeUtil;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 包含属性值的POJO类属性提取对象
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/3 11:19
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FieldValue implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3388630521659133444L;


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

    private Object value;

    public FieldValue(Field field, boolean primary) {
        this.field = field;
        this.primary = primary;
    }

    public FieldValue(Field field, boolean primary, Object value) {
        this.field = field;
        this.primary = primary;
        this.value = value;
    }

    /**
     * 属性值不是null且不为空字符串
     *
     * @return true表示属性值不是null且不为空字符串，否则表示可能为null或空字符串
     */
    public boolean isNotNullVal() {
        if (null != this.value) {
            if (this.value instanceof String) {
                return StringUtils.isNotBlank(this.value.toString());
            }
            return true;
        }

        return false;
    }

    /**
     * 属性值不是null或为空字符串
     *
     * @return true表示属性值不是null或为空字符串，否则表示属性值不是null且不为空字符串
     */
    public boolean isNullVal() {
        if (null == this.value) {
            return true;
        }
        if (this.value instanceof String) {
            return StringUtils.isBlank(this.value.toString());
        }
        return false;
    }

    /**
     * <p>POJO类中的属性在数据库中对应的属性的名字</p>
     * 获取方法如下: 如果colName的值为null就使用name的值
     *
     * @return POJO类中的属性在数据库中对应的属性的名字
     */
    public String getSimpleName() {
        String columnName = FieldUtils.columnName(this.field);
        return columnName;

    }

    /**
     * 获取属性上的列注释
     *
     * @return 属性上的列注释
     */
    public Column column() {
        if (null != this.column) {
            return this.column;
        }
        this.column = AnnotationUtils.findAnnotation(this.field, Column.class);
        return this.column;
    }

    /**
     * 获取属性的SQLType
     *
     * @return 属性的SQLType
     */
    public SQLType sqlType() {
        if (null != this.sqlType) {
            return this.sqlType;
        }
        if (null == this.field || null == this.column) {
            return JDBCType.NULL;
        }
        SQLType sqlType = null;
        String definition = this.column().columnDefinition();

        if (StringUtils.isNotBlank(definition)) {
            sqlType = MysqlType.getByName(definition);
            if (null == sqlType) {
                Matcher matcher = pattern.matcher(definition.trim());
                if (matcher.find()) {
                    // 提取到的第一个组
                    String type = matcher.group(0).trim();
                    sqlType =
                            Arrays.stream(JDBCType.values()).filter(s -> s.getName().equalsIgnoreCase(type)).findFirst().orElse(JDBCType.OTHER);
                }
            }
        } else {
            Class<?> clazz = this.field.getType();
            sqlType = TypeUtil.targetSqlTypeFor(clazz);
        }
        this.sqlType = sqlType;
        return sqlType;
    }
}
