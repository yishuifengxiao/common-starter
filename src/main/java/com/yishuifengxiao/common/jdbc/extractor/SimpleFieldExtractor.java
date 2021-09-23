package com.yishuifengxiao.common.jdbc.extractor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.tool.utils.HumpUtil;

import lombok.extern.slf4j.Slf4j;

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
	private static final Map<String, List<FieldValue>> FIELDS_MAP = new HashMap<>();

	/**
	 * 存储一个类对应的主键属性
	 */
	private static final Map<String, FieldValue> FIELD_MAP = new HashMap<>();

	/**
	 * 存储一个类对应的数据表名字
	 */
	private static final Map<String, String> TABLE_MAP = new HashMap<>();

	/**
	 * 对应一个类里对象属性名字与数据库属性名字
	 */
	private static final Map<String, String> NAME_TABLE = new HashMap<>();

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
	@Override
	public <T> List<FieldValue> extractFiled(Class<T> clazz) {

		synchronized (this) {

			if (null != clazz) {
				List<FieldValue> list = FIELDS_MAP.get(clazz.getName());
				if (null != list) {
					return list;
				}
				list = new ArrayList<>();
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {

					if (this.isExclude(field)) {
						continue;
					}
					FieldValue filedValue = new FieldValue();
					// 属性的名字
					String name = field.getName();
					filedValue.setName(name);
					// 有没有@Column修饰
					Column column = field.getAnnotation(Column.class);
					if (null != column) {
						filedValue.setColName(column.name());
					} else {
						filedValue.setColName(name);
					}

					filedValue.setType(field.getType());

					list.add(filedValue);
				}
				FIELDS_MAP.put(clazz.getName(), list);
				return list;
			}

		}

		return new ArrayList<>();

	}

	/**
	 * 该字段是否不被提取数据信息
	 * 
	 * @param field 字段
	 * @return true表示不被提取，false表示会被提取
	 */
	private boolean isExclude(Field field) {
		Transient sient = field.getAnnotation(Transient.class);
		// 如果被@Transient修饰了就不处理
		if (null != sient) {
			return true;
		}
		// 被Transient 修饰的不处理
		if (Modifier.isTransient(field.getModifiers())) {
			return true;
		}

		// 被final修饰的不处理
		if (Modifier.isFinal(field.getModifiers())) {
			return true;
		}

		// 被static 修饰的不处理
		if (Modifier.isStatic(field.getModifiers())) {
			return true;
		}

		// 被native 修饰的不处理
		if (Modifier.isNative(field.getModifiers())) {
			return true;
		}

		// 被abstract 修饰的不处理
		if (Modifier.isAbstract(field.getModifiers())) {
			return true;
		}

		// 属性为接口
		if (Modifier.isInterface(field.getModifiers())) {
			return true;
		}

		return false;
	}

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
	@Override
	public <T> FieldValue extractPrimaryKey(Class<T> clazz) {
		FieldValue primaryKey = null;
		synchronized (this) {
			if (null != clazz) {
				primaryKey = FIELD_MAP.get(clazz.getName());
				if (null != primaryKey) {
					return primaryKey;
				}

				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					Id idCol = field.getAnnotation(Id.class);
					// 查找 @Id 注解的属性
					if (null != idCol) {
						FieldValue filedValue = new FieldValue();
						filedValue.setName(field.getName());

						// 有没有@Column修饰
						Column column = field.getAnnotation(Column.class);
						if (null != column) {
							filedValue.setColName(column.name());
						} else {
							filedValue.setColName(field.getName());
						}

						filedValue.setType(field.getType());

						FIELD_MAP.put(clazz.getName(), filedValue);
						return filedValue;
					}

				}
			}
		}
		primaryKey = new FieldValue("id", null, null);
		FIELD_MAP.put(clazz.getName(), primaryKey);
		return primaryKey;
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
	 * @param <T> POJO类的类型
	 * @param clazz POJO类
	 * @return OJO类的对应的数据表的名字
	 */
	@Override
	public <T> String extractTableName(Class<T> clazz) {
		synchronized (this) {
			String name = TABLE_MAP.get(clazz.getName());
			if (StringUtils.isNotBlank(name)) {
				return name;
			}
			name = this.getTableName(clazz);
			if (StringUtils.isBlank(name)) {
				name = this.getEntityName(clazz);
				if (StringUtils.isBlank(name)) {
					name = this.getSimpleName(clazz);
				}
			}
			TABLE_MAP.put(clazz.getName(), name);
			return name;
		}

	}

	/**
	 * 提取@Table注解对应的值
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	private <T> String getTableName(Class<T> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		return null != table ? table.name() : null;
	}

	/**
	 * 提取@Entity注解对应的值
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	private <T> String getEntityName(Class<T> clazz) {

		Entity entity = clazz.getAnnotation(Entity.class);
		return null != entity ? entity.name() : null;

	}

	/**
	 * 获取POJO对象的名字
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	private <T> String getSimpleName(Class<T> clazz) {
		String simpleName = clazz.getSimpleName();
		if (simpleName != null) {
			return HumpUtil.underscoreName(simpleName);
		}
		return null;
	}

	/**
	 * <p>
	 * 提取对象中指定属性的值
	 * </p>
	 * 
	 * @param data      提取对象
	 * @param fieldName 属性的名字
	 * @return 属性的值
	 */
	@Override
	public synchronized Object extractValue(Object data, String fieldName) {
		if (null == data || null == fieldName) {
			return null;
		}
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = data.getClass().getMethod(getter, new Class[] {});
			method.setAccessible(true);
			Object value = method.invoke(data, new Object[] {});
			return value;
		} catch (Exception e) {
			log.warn("根据属性名获取属性值时出现问题，出现问题的原因为 {}", e.getMessage());
		}
		return null;
	}

	/**
	 * 根据POJO类属性的名字提取其在数据库库里对应的列的名字
	 * 
	 * @param <T>   POJO类的类型
	 * @param clazz POJO类
	 * @param name  POJO类属性的名字
	 * @return POJO类属性再数据库里对应的列的名字
	 */
	@Override
	public <T> String extractColNameByName(Class<T> clazz, String name) {
		if (null == clazz || StringUtils.isBlank(name)) {
			return null;
		}
		synchronized (this) {
			String key = new StringBuilder(clazz.getName()).append(name).toString();
			String colName = NAME_TABLE.get(key);
			if (null != colName) {
				return colName;
			}
			List<FieldValue> list = this.extractFiled(clazz);

			for (FieldValue field : list) {
				if (StringUtils.equalsIgnoreCase(field.getName(), name.trim())) {
					colName = field.getSimpleName();
					break;
				}
			}
			NAME_TABLE.put(key, colName);
			return colName;
		}
	}

}
