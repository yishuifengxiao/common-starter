package com.yishuifengxiao.common.jdbc.util;

import com.yishuifengxiao.common.tool.lang.TextUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 属性工具
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/3 11:58
 * @since 1.0.0
 */
@Slf4j
public class FieldUtils {


    /**
     * 判断该属性是否为主键
     *
     * @param field 属性
     * @return 若为主键返回为true，否则为false
     */
    public static boolean isPrimary(Field field) {
        if (null == field) {
            return false;
        }
        Id id = AnnotationUtils.findAnnotation(field, Id.class);
        if (null != id) {
            return true;
        }
        Column column = AnnotationUtils.findAnnotation(field, Column.class);
        if (null != column && "id".equalsIgnoreCase(column.name())) {
            return true;
        }
        return "id".equalsIgnoreCase(field.getName());
    }

    /**
     * 获取pojo属性对应的sql列名
     *
     * @param field pojo属性
     * @return sql列名
     */
    public static String columnName(Field field) {
        if (null == field) {
            return null;
        }
        Column column = AnnotationUtils.findAnnotation(field, Column.class);
        if (null != column && StringUtils.isNotBlank(column.name())) {
            return column.name();
        }
        String fieldName = field.getName();
        return TextUtil.underscoreName(fieldName);
    }

    public static Object extractVal(Field field, Object val) {
        if (null == field || null == val) {
            return null;
        }
        try {
            ReflectionUtils.makeAccessible(field);
            return field.get(val);
        } catch (Exception e) {
            log.warn("There was a problem extracting the value of attribute {} from data {}, the "
                    + "problem " +
                    "is {}", field, val, e);
        }
        return null;
    }


}
