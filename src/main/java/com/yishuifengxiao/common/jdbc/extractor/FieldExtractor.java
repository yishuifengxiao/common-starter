package com.yishuifengxiao.common.jdbc.extractor;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;

import java.util.List;

/**
 * <p>
 * 属性提取器
 * </p>
 * 主要作用是从POJO类里提取出各种信息
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface FieldExtractor {
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
    <T> List<FieldValue> extractFieldValue(T t);

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
    <T> List<FieldValue> extractFiled(Class<T> clazz);

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
    <T> String extractTableName(Class<T> clazz);

    /**
     * <p>
     * 提取一个POJO类的主键字段属性
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
     * @return POJO类的主键字段属性
     */
    <T> FieldValue extractPrimaryFiled(Class<T> clazz);
}
