package com.yishuifengxiao.common.jdbc.extractor;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;

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
	 * 提取一个POJO类所有字段属性
	 * </p>
	 *
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
	 * 
	 * @param <T>   POJO类的类型
	 * @param clazz POJO类
	 * @return POJO类所有字段属性
	 */
	<T> List<FieldValue> extractFiled(Class<T> clazz);

	/**
	 * <p>
	 * 提取一个POJO类的主键字段信息
	 * </p>
	 * 根据POJO类属性上的@Id 注解查找，如果没有找到，默认返回值的为 id
	 * 
	 * 
	 * @param <T>   POJO类的类型
	 * @param clazz POJO类
	 * @return POJO类的主键字段信息
	 */
	<T> FieldValue extractPrimaryKey(Class<T> clazz);

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
	 * @param <T> POJO类的类型
	 * @param clazz POJO类
	 * @return OJO类的对应的数据表的名字
	 */
	<T> String extractTableName(Class<T> clazz);

	/**
	 * <p>
	 * 提取对象中指定属性的值
	 * </p>
	 * 
	 * @param data      提取对象
	 * @param fieldName 属性的名字
	 * @return 属性的值
	 */
	Object extractValue(Object data, String fieldName);

	/**
	 * 根据POJO类属性的名字提取其在数据库库里对应的列的名字
	 * 
	 * @param <T>   POJO类的类型
	 * @param clazz POJO类
	 * @param name  POJO类属性的名字
	 * @return POJO类属性再数据库里对应的列的名字
	 */
	<T> String extractColNameByName(Class<T> clazz, String name);
}
