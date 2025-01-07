package com.yishuifengxiao.common.jdbc.translator.impl;

import com.yishuifengxiao.common.jdbc.JdbcError;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.translator.InsertTranslator;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统插入动作解释器
 * </p>
 * 负责执行插入相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleInsertTranslator implements InsertTranslator {

    /**
     * 生成数据插入的sql语句
     *
     * @param table       表名
     * @param fieldValues 属性数据
     * @return
     */

    @Override
    public String insert(String table, List<FieldValue> fieldValues) {
        List<String> placeholders = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();

        fieldValues.stream().forEach(fieldValue -> {
            placeholders.add("?");
            fieldNames.add(fieldValue.getSimpleName());
        });
        ValidateUtils.isTrue(!placeholders.isEmpty(), JdbcError.PARAMS_IS_ALL_NULL);

        StringBuilder sql =
                new StringBuilder("insert into ").append(table).append(" ( ").append(fieldNames.stream()
                                .collect(Collectors.joining(","))).append(" ) values ( ").
                        append(placeholders.stream().collect(Collectors.joining(","))).append(" ) ");
        return sql.toString();
    }


}
