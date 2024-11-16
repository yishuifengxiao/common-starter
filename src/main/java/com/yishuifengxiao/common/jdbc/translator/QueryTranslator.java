package com.yishuifengxiao.common.jdbc.translator;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Slice;

import java.util.List;

/**
 * <p>
 * 查询动作解释器
 * </p>
 * 负责执行查询相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface QueryTranslator extends ExecuteTranslator {


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
    String findAll(String table, List<FieldValue> fieldValues, boolean like,
                   List<Order> orders,
                   Slice slice);


}
