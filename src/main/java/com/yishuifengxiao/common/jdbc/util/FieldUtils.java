package com.yishuifengxiao.common.jdbc.util;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

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
     * 判断给定的类是否为基本数据类型或其对应的包装类
     *
     * @param clazz 待判断的类对象
     * @return 如果是基本数据类型或其包装类则返回true，否则返回false
     */
    public static <T> boolean isBasicResult(Class<T> clazz) {

        // 检查是否为基本数据类型或常见的包装类
        return clazz.isPrimitive() || clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(Double.class)
                || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Boolean.class)
                || clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(Short.class)
                || clazz.isAssignableFrom(Character.class);
    }


}
