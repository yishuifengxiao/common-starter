package com.yishuifengxiao.common.jdbc.translator.impl;

import com.yishuifengxiao.common.jdbc.JdbcError;
import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.translator.UpdateTranslator;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统更新动作解释器
 * </p>
 * 负责执行更新相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleUpdateTranslator implements UpdateTranslator {

    /**
     * 生成根据主键更新数据的sql
     *
     * @param table       表名
     * @param fieldValues pojo类属性数据列表
     * @return 更新数据的sql
     */

    @Override
    public String updateByPrimaryKey(String table, FieldValue primaryKey, List<FieldValue> fieldValues) {
        List<String> placeholders = new ArrayList<>();
        fieldValues.stream().filter(v -> !v.isPrimary()).forEach(fieldValue -> {
            placeholders.add(fieldValue.getSimpleName() + " = ? ");
        });
        ValidateUtils.isTrue(!placeholders.isEmpty(), JdbcError.PARAMS_IS_ALL_NULL);
        StringBuilder sql =
                new StringBuilder("update ").append(table).append(" set ").append(placeholders.stream()
                        .collect(Collectors.joining(" , ")))
                        .append(" " + " where ").append(primaryKey.getSimpleName()).append(" = ? ");
        return sql.toString();
    }


}
