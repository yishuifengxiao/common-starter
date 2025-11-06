package com.yishuifengxiao.common.jdbc.translator;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Slice;

import java.util.List;

/**
 * sql语句转换器sql语句生成器
 * 主要功能时根据pojo类属性数据列表生成对应的sql语句
 *
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SqlTranslator {
    /**
     * 生成数据插入的sql语句
     *
     * @param table       表名
     * @param fieldValues pojo类属性数据列表
     * @return
     */
    String insert(String table, List<FieldValue> fieldValues);

    /**
     * 生成根据主键更新数据的sql
     *
     * @param table       表名
     * @param primaryKey  主键属性
     * @param fieldValues pojo类属性数据列表
     * @return 更新数据的sql
     */
    String updateByPrimaryKey(String table, FieldValue primaryKey, List<FieldValue> fieldValues);

    /**
     * 根据条件生成根据主键删除数据的sql
     *
     * @param table      表名
     * @param primaryKey 主键名称
     * @param values     主键值，单个值时使用等值匹配，多个值时使用in匹配
     * @return 根据主键批量删除数据的sql
     */
    String deleteByPrimaryKeys(String table, String primaryKey, List<Object> values);

    /**
     * 根据条件生成数据查询sql语句
     *
     * @param table       表名
     * @param fieldValues pojo类属性数据列表
     * @param like        是否对字符串属性进行模糊查询
     * @param orders      排序条件
     * @param slice       分页参数
     * @return 查询sql语句
     */
    String findAll(String table, List<FieldValue> fieldValues, boolean like, List<Order> orders, Slice slice);
}
