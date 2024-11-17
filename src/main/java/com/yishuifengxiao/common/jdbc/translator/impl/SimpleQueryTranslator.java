package com.yishuifengxiao.common.jdbc.translator.impl;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.translator.QueryTranslator;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.Slice;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 简单实现的查询动作解释器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleQueryTranslator implements QueryTranslator {


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
    @Override
    public String findAll(String table, List<FieldValue> fieldValues, boolean like, List<Order> orders, Slice slice) {
        //@formatter:off
        List<String> placeholders = createSql(fieldValues, like);
        StringBuilder sql = new StringBuilder(" select * from ").append(table);
        if (!placeholders.isEmpty()) {
            sql.append(" where  ").append(placeholders.stream().collect(Collectors.joining(" and "
            )));
        }
        if (CollUtil.isNoneEmpty(orders)) {
            sql.append(" order by ").append(orders.stream().filter(Objects::nonNull)
                    .filter(s -> StringUtils.isNotBlank(s.getOrderName()))
                    .map(s -> s.getOrderName() + " " + s.direction())
                    .collect(Collectors.joining(" , ")));
        }
        if (null != slice && (null != slice.getNum())) {
            if (null != slice.getSize()) {
                sql.append(" limit ").append(slice.startOffset()).append(",").append(slice.size());
            } else {
                sql.append(" limit ").append(slice.size());
            }
        }
        //@formatter:on
        return sql.toString();
    }

    private static List<String> createSql(List<FieldValue> fieldValues, boolean like) {
        List<String> placeholders = new ArrayList<>();
        //@formatter:off
        fieldValues.stream().forEach(fieldValue -> {
            if (like && fieldValue.isNotNullVal()
                    && fieldValue.getValue() instanceof String
                    && !fieldValue.isPrimary()) {
                placeholders.add(fieldValue.getSimpleName() + " like  CONCAT('%',?,'%') ");
            } else {
                placeholders.add(fieldValue.getSimpleName() + " = ? ");
            }
        });
        //@formatter:on
        return placeholders;
    }


}
