package com.yishuifengxiao.common.utils;


/**
 * BeanUtil类是一个用于处理Java Bean对象属性的工具类。
 * 提供了对象属性克隆的功能，简化了对象属性复制的操作。
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class BeanUtils {

    /**
     * 克隆对象属性的工具方法
     * 此方法利用Spring框架的BeanUtils工具类，实现源对象到目标对象的属性复制。
     * 注意：此方法仅复制同名的属性，且属性类型需要兼容。
     *
     * @param source 源对象，提供要复制的属性值
     * @param traget 目标对象，接收源对象的属性值
     * @param <T>    泛型类型，表示目标对象的类型
     * @return 返回克隆后的目标对象，如果源对象或目标对象为null，则返回null
     */
    public static <T> T clone(Object source, T traget) {
        // 检查源对象和目标对象是否为null，如果任一为null则返回null
        // 这是一个防御性编程实践，避免NullPointerException
        if (null == source || null == traget) {
            return null;
        }
        // 使用Spring框架的BeanUtils工具类复制源对象的属性到目标对象
        // 此处会忽略null值的属性，且不会复制只读属性
        org.springframework.beans.BeanUtils.copyProperties(source, traget);
        // 返回克隆后的目标对象
        // 注意：返回的是同一个对象实例，只是其属性已被更新
        return traget;
    }
}