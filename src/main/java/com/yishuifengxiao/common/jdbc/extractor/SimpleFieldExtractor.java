package com.yishuifengxiao.common.jdbc.extractor;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.util.FieldUtils;
import com.yishuifengxiao.common.tool.bean.ClassUtil;
import com.yishuifengxiao.common.tool.lang.TextUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 系统属性提取器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleFieldExtractor implements FieldExtractor {


    /**
     * 存储一个类所有对应的属性
     */
    private static final Map<String, List<FieldValue>> FIELDS_MAP = new ConcurrentHashMap<>();

    /**
     * 存储一个类对应的数据表名字
     */
    private static final Map<String, String> TABLE_MAP = new ConcurrentHashMap<>();

    /**
     * <p>
     * 提取一个POJO类所有字段属性以及对应的值
     * </p>
     * <p>
     * 【注意】下面的字段属性会不会被提取出来
     * <ul>
     * <li>被final修饰的属性</li>
     * <li>被 @Transient 修饰的属性</li>
     * <li>被 transient 修饰的属性</li>
     * <li>被static 修饰</li>
     * <li>被native 修饰的不处理</li>
     * <li>被abstract 修饰的不处理</li>
     * <li>属性为接口</li>
     * </ul>
     *
     * @param <T> POJO类的类型
     * @param t   待提取数据的pojo类
     * @return POJO类所有字段属性
     */
    @Override
    public <T> List<FieldValue> extractFieldValue(T t) {
        if (null == t) {
            return Collections.EMPTY_LIST;
        }
        synchronized (this) {
            List<FieldValue> fields = this.extractFiled(t.getClass());
            return fields.parallelStream().map(field -> {
                Object value = FieldUtils.extractVal(field.getField(), t);
                return field.setValue(value);

            }).collect(Collectors.toList());
        }
    }

    /**
     * <p>
     * 提取一个POJO类所有字段属性
     * </p>
     * <p>
     * 【注意】下面的字段属性会不会被提取出来
     * <ul>
     * <li>被final修饰的属性</li>
     * <li>被 @Transient 修饰的属性</li>
     * <li>被 transient 修饰的属性</li>
     * <li>被static 修饰</li>
     * <li>被native 修饰的不处理</li>
     * <li>被abstract 修饰的不处理</li>
     * <li>属性为接口</li>
     * </ul>
     *
     * @param <T>   POJO类的类型
     * @param clazz POJO类
     * @return POJO类所有字段属性
     */
    @Override
    public <T> List<FieldValue> extractFiled(Class<T> clazz) {
        if (null == clazz) {
            return Collections.EMPTY_LIST;
        }
        List<FieldValue> list = FIELDS_MAP.get(clazz.getName());
        if (null != list) {
            return list;
        }
        synchronized (this) {
            List<Field> fields = ClassUtil.fields(clazz, true);
            List<FieldValue> fieldValues =
                    fields.parallelStream().map(field -> new FieldValue(field,
                            FieldUtils.isPrimary(field))).collect(Collectors.toList());
            FIELDS_MAP.put(clazz.getName(), fieldValues);
            return fieldValues;
        }
    }


    /**
     * <p>
     * 提取一个POJO类的对应的数据表的名字
     * </p>
     * 查找策略如下
     * <ul>
     * <li>先提取类上面 @Table 注解的值</li>
     * <li>其次提取类注解上 @Entity 注解的值</li>
     * <li>最后使用当前类的名字，然后将其转化为驼峰命名</li>
     * </ul>
     *
     * @param <T>   POJO类的类型
     * @param clazz POJO类
     * @return OJO类的对应的数据表的名字
     */
    @Override
    public <T> String extractTableName(Class<T> clazz) {
        String name = TABLE_MAP.get(clazz.getName());
        if (null != name) {
            return name;
        }
        synchronized (this) {
            name = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, Table.class)).map(Table::name).orElse(null);
            if (StringUtils.isBlank(name)) {
                name = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, Entity.class)).map(Entity::name).orElse(null);
                if (StringUtils.isBlank(name)) {
                    name = TextUtil.underscoreName(clazz.getSimpleName());
                }
            }
            TABLE_MAP.put(clazz.getName(), name);
            return name;
        }

    }


}
