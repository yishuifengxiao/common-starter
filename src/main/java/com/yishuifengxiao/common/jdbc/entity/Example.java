package com.yishuifengxiao.common.jdbc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.tool.collections.DataUtil;

/**
 * 判断条件
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class Example implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8193315652356670762L;

	private final List<Condition> list = new ArrayList<>();

	public static Example instance() {
		return new Example();
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 =
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example andEqual(String name, Object value) {

		list.add(Condition.andEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#60;&#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example andNotEqual(String name, Object value) {

		list.add(Condition.andNotEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example andGreater(String name, Object value) {
		list.add(Condition.andGreater(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#62;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example andGreaterEqual(String name, Object value) {
		list.add(Condition.andGreaterEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#60;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example andLess(String name, Object value) {
		list.add(Condition.andLess(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 &#60;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */

	public Example andLessEqual(String name, Object value) {
		list.add(Condition.andLessEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public Example andIsNull(String name) {
		list.add(Condition.andIsNull(name));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 !isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public Example andNotNull(String name) {
		list.add(Condition.andNotNull(name));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 like
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example andLike(String name, Object value) {
		list.add(Condition.andLike(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	public Example andIn(String name, Object... values) {
		list.add(Condition.andIn(name, values));
		return this;
	}

	/**
	 * 生成一个连接条件为and的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	@SuppressWarnings("rawtypes")
	public Example andIn(String name, List values) {
		list.add(Condition.andIn(name, values));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 =
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orEqual(String name, Object value) {
		list.add(Condition.orEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#60;&#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orNotEqual(String name, Object value) {
		list.add(Condition.orNotEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#62;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orGreater(String name, Object value) {
		list.add(Condition.orGreater(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#62;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orGreaterEqual(String name, Object value) {
		list.add(Condition.orGreaterEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#60;
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orLess(String name, Object value) {
		list.add(Condition.orLess(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 &#60;=
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orLessEqual(String name, Object value) {
		list.add(Condition.orLessEqual(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public Example orIsNull(String name) {
		list.add(Condition.orIsNull(name));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 !isnull()
	 * 
	 * @param name 属性名称【对应POJO类的属性名字】
	 * @return 比较条件
	 */
	public Example orNotNull(String name) {
		list.add(Condition.orNotNull(name));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 like
	 * 
	 * @param name  属性名称【对应POJO类的属性名字】
	 * @param value 比较值
	 * @return 比较条件
	 */
	public Example orLike(String name, Object value) {
		list.add(Condition.orLike(name, value));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	public Example orIn(String name, Object... values) {
		list.add(Condition.orIn(name, values));
		return this;
	}

	/**
	 * 生成一个连接条件为or的比较语句，比较方式为 in
	 * 
	 * @param name   属性名称【对应POJO类的属性名字】
	 * @param values 比较值
	 * @return 比较条件
	 */
	@SuppressWarnings("rawtypes")
	public Example orIn(String name, List values) {
		list.add(Condition.orIn(name, values));
		return this;
	}

	/**
	 * 获取所有的筛选条件
	 * 
	 * @return 所有的筛选条件
	 */
	public List<Condition> toCondition() {
		return DataUtil.stream(list).filter(Objects::nonNull).filter(t -> StringUtils.isNotBlank(t.getName()))
				.collect(Collectors.toList());
	}

}
