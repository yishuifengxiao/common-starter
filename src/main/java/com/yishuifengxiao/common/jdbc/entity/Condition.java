package com.yishuifengxiao.common.jdbc.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 筛选条件
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class Condition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7126780721609499396L;

	/**
	 * 语句连接方式
	 */
	private Link link = Link.AND;

	/**
	 * 比较方式
	 */
	private Type type;

	/**
	 * 比较的属性名字，对应POJO类的属性
	 */
	private String name;

	/**
	 * 待比较的数据
	 */
	private Object value;

	/**
	 * 设置筛选对象的语句连接方式
	 * 
	 * @param link 语句连接方式
	 * @return 筛选对象
	 */
	public Condition link(Link link) {
		this.link = link;
		return this;
	}

	/**
	 * 设置筛选对象的 比较方式
	 * 
	 * @param type 比较方式
	 * @return 筛选对象
	 */
	public Condition type(Type type) {
		this.type = type;
		return this;
	}

	/**
	 * 设置筛选对象的比较的属性名字，对应POJO类的属性
	 * 
	 * @param name 比较的属性名字，对应POJO类的属性
	 * @return 筛选对象
	 */
	public Condition name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 设置筛选对象的待比较的数据
	 * 
	 * @param value 待比较的数据
	 * @return 筛选对象
	 */
	public Condition value(Object value) {
		this.value = value;
		return this;
	}

	/**
	 * 语句连接方式
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum Link {
		/**
		 * and 连接
		 */
		AND,
		/**
		 * or 连接
		 */
		OR
	}

	/**
	 * 比较方式
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum Type {
		/**
		 * 相等 =
		 */
		EQUAL,
		/**
		 * 不等于 !=
		 */
		NOT_EQUAL,
		/**
		 * 大于 &#62;
		 */
		GREATER,
		/**
		 * 大于或等于&#62;=
		 */
		GREATER_EQUAL,
		/**
		 * 小于 &#60;
		 */
		LESS,
		/**
		 * 小于或等于 &#60;=
		 */
		LESS_EQUAL,
		/**
		 * 为null ,isnull()
		 */
		IS_NULL,
		/**
		 * 不为空 !isnull()
		 */
		NOT_NULL,
		/**
		 * 模糊查询 like
		 */
		LIKE,
		/**
		 * in 查询，会替换成 (colName = ? or colName = ? )的形式
		 */
		IN
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 =
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition andEqual(String name, Object value) {
		return new Condition(Link.AND, Type.EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#60;&#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition andNotEqual(String name, Object value) {
		return new Condition(Link.AND, Type.NOT_EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition andGreater(String name, Object value) {
		return new Condition(Link.AND, Type.GREATER, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#62;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition andGreaterEqual(String name, Object value) {
		return new Condition(Link.AND, Type.GREATER_EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#60;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition andLess(String name, Object value) {
		return new Condition(Link.AND, Type.LESS, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为&#60;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */

	public static Condition andLessEqual(String name, Object value) {
		return new Condition(Link.AND, Type.LESS_EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public static Condition andIsNull(String name) {
		return new Condition(Link.AND, Type.IS_NULL, name, serialVersionUID);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 !isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public static Condition andNotNull(String name) {
		return new Condition(Link.AND, Type.NOT_NULL, name, serialVersionUID);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 like
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition andLike(String name, Object value) {
		return new Condition(Link.AND, Type.LIKE, name, value);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	public static Condition andIn(String name, Object... values) {
		return new Condition(Link.AND, Type.IN, name, values);
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	@SuppressWarnings("rawtypes")
	public static Condition andIn(String name, List values) {
		return new Condition(Link.AND, Type.IN, name, values);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 =
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orEqual(String name, Object value) {
		return new Condition(Link.OR, Type.EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#60;&#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orNotEqual(String name, Object value) {
		return new Condition(Link.OR, Type.NOT_EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orGreater(String name, Object value) {
		return new Condition(Link.OR, Type.GREATER, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#62;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orGreaterEqual(String name, Object value) {
		return new Condition(Link.OR, Type.GREATER_EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#60;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orLess(String name, Object value) {
		return new Condition(Link.OR, Type.LESS, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#60;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orLessEqual(String name, Object value) {
		return new Condition(Link.OR, Type.LESS_EQUAL, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public static Condition orIsNull(String name) {
		return new Condition(Link.OR, Type.IS_NULL, name, serialVersionUID);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 !isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public static Condition orNotNull(String name) {
		return new Condition(Link.OR, Type.NOT_NULL, name, serialVersionUID);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 like
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public static Condition orLike(String name, Object value) {
		return new Condition(Link.OR, Type.LIKE, name, value);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	public static Condition orIn(String name, Object... values) {
		return new Condition(Link.OR, Type.IN, name, values);
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	@SuppressWarnings("rawtypes")
	public static Condition orIn(String name, List values) {
		return new Condition(Link.OR, Type.IN, name, values);
	}

	private Condition() {

	}

	private Condition(Link link, Type type, String name, Object value) {
		this.link = link;
		this.type = type;
		this.name = name;
		this.value = value;
	}

}
