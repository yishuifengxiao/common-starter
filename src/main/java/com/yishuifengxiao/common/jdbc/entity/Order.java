package com.yishuifengxiao.common.jdbc.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 排序条件
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3667336189389139540L;

	/**
	 * 排序的字段名字<br/>
	 * 【注意】 必须为POJO的字段的名字
	 */
	private String orderName;

	/**
	 * 排序方向<br/>
	 * ASC 为升序，DESC为 降序
	 */
	private Direction direction;

	/**
	 * 将排序方式设置为降序
	 * 
	 * @return
	 */
	public Order desc() {
		this.direction = Direction.DESC;
		return this;
	}

	/**
	 * 将排序方式设置为升序
	 * 
	 * @return
	 */
	public Order asc() {
		this.direction = Direction.ASC;
		return this;
	}

	/**
	 * 构建一个升序的排序对象
	 * 
	 * @param name 排序属性名称【必须为POJO类属性名称】
	 * @return
	 */
	public static Order asc(String name) {
		return Order.of(name, Direction.ASC);
	}

	/**
	 * 构建一个降序的排序对象
	 * 
	 * @param name 排序属性名称【必须为POJO类属性名称】
	 * @return
	 */
	public static Order desc(String name) {
		return Order.of(name, Direction.DESC);
	}

	/**
	 * 设置排序字段名称
	 * 
	 * @param name 排序字段名称【必须为POJO类的属性名字】
	 * @return
	 */
	public Order name(String name) {
		this.orderName = name;
		return this;
	}

	public static enum Direction {
		/**
		 * 升序
		 */
		ASC,
		/**
		 * 降序
		 */
		DESC

	}

	/**
	 * 构造一个排序对象
	 * 
	 * @param orderName 排序的字段名字，【注意】 必须为数据库对应的字段的名字
	 * @param direction 排序方向
	 * @return
	 */
	public static Order of(String orderName, Direction direction) {
		return new Order(orderName, direction);
	}

	/**
	 * 构造一个排序对象
	 * 
	 * @param direction 排序方向
	 * @return
	 */
	public static Order of(Direction direction) {
		return new Order(null, direction);
	}

	/**
	 * 构造一个排序对象<br/>
	 * 默认为升序
	 * 
	 * @param orderName 排序的字段名字，【注意】 必须为数据库对应的字段的名字
	 * @return
	 */
	public static Order of(String orderName) {
		return new Order(orderName, Direction.ASC);
	}
}
