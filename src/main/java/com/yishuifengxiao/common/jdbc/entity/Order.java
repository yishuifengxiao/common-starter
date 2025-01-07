package com.yishuifengxiao.common.jdbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 排序条件
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
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
     * <p>
     * 排序的字段名字
     * </p>
     * 【注意】 必须为POJO的字段的名字
     */
    private String orderName;

    /**
     * <p>排序方向</p>
     * ASC 为升序，DESC为 降序
     */
    private Direction direction;

    /**
     * 将排序方式设置为降序
     *
     * @return 排序条件
     */
    public Order desc() {
        this.direction = Direction.DESC;
        return this;
    }

    /**
     * 排序方向
     *
     * @return
     */
    public String direction() {
        return (null == this.direction || this.direction == Direction.ASC) ? "ASC" : "DESC";
    }

    /**
     * 将排序方式设置为升序
     *
     * @return 排序条件
     */
    public Order asc() {
        this.direction = Direction.ASC;
        return this;
    }

    /**
     * 构建一个升序的排序对象
     *
     * @param name 排序属性名称【必须为POJO类属性名称】
     * @return 排序条件
     */
    public static Order asc(String name) {
        return Order.of(name, Direction.ASC);
    }

    /**
     * 构建一个降序的排序对象
     *
     * @param name 排序属性名称【必须为POJO类属性名称】
     * @return 排序条件
     */
    public static Order desc(String name) {
        return Order.of(name, Direction.DESC);
    }

    /**
     * 设置排序字段名称
     *
     * @param name 排序字段名称【必须为POJO类的属性名字】
     * @return 排序条件
     */
    public Order name(String name) {
        this.orderName = name;
        return this;
    }

    /**
     * 排序方向
     *
     * @author qingteng
     * @version 1.0.0
     * @since 1.0.0
     */
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
     * @return 排序条件
     */
    public static Order of(String orderName, Direction direction) {
        return new Order(orderName, direction);
    }

    /**
     * 构造一个排序对象
     *
     * @param direction 排序方向
     * @return 排序条件
     */
    public static Order of(Direction direction) {
        return new Order(null, direction);
    }

    /**
     * <p>
     * 构造一个排序对象
     * </p>
     * 默认为升序
     *
     * @param orderName 排序的字段名字，【注意】 必须为数据库对应的字段的名字
     * @return 排序条件
     */
    public static Order of(String orderName) {
        return new Order(orderName, Direction.ASC);
    }
}
