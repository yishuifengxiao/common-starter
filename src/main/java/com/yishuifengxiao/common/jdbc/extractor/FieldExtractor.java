package com.yishuifengxiao.common.jdbc.extractor;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;

/**
 * 属性提取器
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public interface FieldExtractor {

	/**
	 * 提取一个POJO类所有字段属性<br/>
	 * <br/>
	 * 【注意】下面的字段属性会不会被提取出来<br/>
	 * 1 被final修饰的属性<br/>
	 * 2 被 @Transient 修饰的属性<br/>
	 * 3 被static 修饰<br/>
	 * 4 被native 修饰的不处理<br/>
	 * 5 被abstract 修饰的不处理<br/>
	 * 6 属性为接口
	 * 
	 * @see @Transient
	 * @param <T>
	 * @param clazz POJO类
	 * @return POJO类所有字段属性
	 */
	<T> List<FieldValue> extractFiled(Class<T> clazz);

	/**
	 * 提取一个POJO类的主键字段信息<br/>
	 * 根据POJO类属性上的@Id 注解查找，如果没有找到，默认返回值的为 id
	 * 
	 * @see @Id
	 * @param <T>
	 * @param clazz POJO类
	 * @return POJO类的主键字段信息
	 */
	<T> FieldValue extractPrimaryKey(Class<T> clazz);

	/**
	 * 提取一个POJO类的对应的数据表的名字<br/>
	 * 查找策略如下:<br/>
	 * 1 先提取类上面 @Table 注解的值<br/>
	 * 2 其次提取类注解上 @Entity 注解的值<br/>
	 * 3 最后使用当前类的名字，然后将其转化为驼峰命名
	 * 
	 * @param <T>
	 * @param clazz POJO类
	 * @return OJO类的对应的数据表的名字
	 */
	<T> String extractTableName(Class<T> clazz);

	/**
	 * 提取对象中指定属性的值
	 * 
	 * @param data      提取对象
	 * @param fieldName 属性的名字
	 * @return 属性的值
	 */
	Object extractValue(Object data, String fieldName);

	/**
	 * 根据POJO类属性的名字提取其再数据库库里对应的列的名字
	 * 
	 * @param <T>
	 * @param clazz POJO类
	 * @param name  POJO类属性的名字
	 * @return POJO类属性再数据库里对应的列的名字
	 */
	<T> String extractColNameByName(Class<T> clazz, String name);
}
