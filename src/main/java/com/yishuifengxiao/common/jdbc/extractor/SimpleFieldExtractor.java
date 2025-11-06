package com.yishuifengxiao.common.jdbc.extractor;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.util.FieldUtils;
import com.yishuifengxiao.common.tool.bean.ClassUtil;
import com.yishuifengxiao.common.tool.lang.TextUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
            return Collections.emptyList();
        }

        List<FieldValue> fields;
        synchronized (this) {
            fields = this.extractFiled(t.getClass()); // 修正拼写错误
        }

        return fields.stream().map(field -> {
            String fieldName = field.getField().getName(); // 避免重复调用 getName()
            try {
                Object value = ClassUtil.extractValue(t, fieldName);
                return field.setValue(value);
            } catch (Exception e) {
                // 使用日志框架替代 System.err
                log.warn("Failed to extract field: {}, error: {}", fieldName, e.getMessage(), e);
                // 根据业务需要决定是跳过该字段还是抛出异常
                return field; // 或者 return null 并在 collect 前 filter 掉
            }
        }).collect(Collectors.toList());
    }


    /**
     * <p>
     * 提取一个POJO类所有字段属性
     * </p>
     * <p>
     * 【注意】下面的字段属性不会被提取出来：
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
            return Collections.emptyList();
        }

        return FIELDS_MAP.computeIfAbsent(clazz.getName(), key -> {
            try {
                List<Field> fields = ClassUtil.fields(clazz, true);
                return fields.stream().filter(field -> !Modifier.isStatic(field.getModifiers())).filter(field -> !Modifier.isFinal(field.getModifiers())).filter(field -> !Modifier.isNative(field.getModifiers())).filter(field -> !Modifier.isAbstract(field.getModifiers())).filter(field -> !field.getType().isInterface()).filter(field -> !Modifier.isTransient(field.getModifiers())).filter(field -> field.getAnnotation(Transient.class) == null).map(field -> new FieldValue(field, FieldUtils.isPrimary(field))).collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract fields from class: " + clazz.getName(), e);
            }
        });
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
     * @return POJO类的对应的数据表的名字
     */
    @Override
    public <T> String extractTableName(Class<T> clazz) {

        // 使用 computeIfAbsent 简化缓存逻辑并确保线程安全
        return TABLE_MAP.computeIfAbsent(clazz.getName(), key -> {
            // 尝试获取@Table注解中的名称
            String name = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, Table.class)).map(Table::name).filter(StringUtils::isNotBlank).orElse(null);

            // 若未找到，则尝试@Entity注解
            if (StringUtils.isBlank(name)) {
                name = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, Entity.class)).map(Entity::name).filter(StringUtils::isNotBlank).orElse(null);
            }

            // 最终回退到类名转下划线格式
            if (StringUtils.isBlank(name)) {
                name = TextUtil.underscoreName(clazz.getSimpleName());
            }

            return name;
        });
    }

    /**
     * <p>
     * 提取一个POJO类的主键属性
     * </p>
     * 查找策略如下
     * <ul>
     * <li>先提取类上面 @Id 注解的属性</li>
     * <li>其次提取类注解上 @Column 注解的值为id的属性</li>
     * <li>最后提取属性名为id的属性</li>
     * </ul>
     *
     * @param <T>   POJO类的类型
     * @param clazz POJO类
     * @return OJO类的主键属性
     */
    @Override
    public <T> FieldValue extractPrimaryFiled(Class<T> clazz) {
        if (null == clazz) {
            return null;
        }
        List<FieldValue> fieldValues = extractFiled(clazz);
        return fieldValues.stream().filter(FieldValue::isPrimary).findFirst().orElse(null);
    }

}
