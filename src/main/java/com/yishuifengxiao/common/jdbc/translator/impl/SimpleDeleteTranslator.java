package com.yishuifengxiao.common.jdbc.translator.impl;

import com.yishuifengxiao.common.jdbc.translator.DeleteTranslator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统删除动作解释器
 * </p>
 * 负责执行删除相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleDeleteTranslator implements DeleteTranslator {

    /**
     * 根据条件生成根据主键批量删除数据的sql
     *
     * @param table      表名
     * @param primaryKey 主键名称
     * @param values     主键值
     * @return 根据主键批量删除数据的sql
     */

    @Override
    public String deleteByPrimaryKeys(String table, String primaryKey, List<Object> values) {
        String params = values.stream().filter(Objects::nonNull).map(v -> {
            if (v instanceof String) {
                if (StringUtils.isNotBlank(v.toString())) {
                    return "'" + ((String) v).trim() + "'";
                }
            }
            return String.valueOf(v);

        }).collect(Collectors.joining(" , "));
        StringBuilder sql =
                new StringBuilder("delete from  ").append(table).append(" where ").append(primaryKey)
                        .append(" in (").append(params).append(" ) ");
        return sql.toString();
    }


}
