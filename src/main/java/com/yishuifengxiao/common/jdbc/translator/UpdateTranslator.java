package com.yishuifengxiao.common.jdbc.translator;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;

import java.util.List;

/**
 * <p>
 * 更新动作解释器
 * </p>
 * 负责执行更新相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */

public interface UpdateTranslator extends ExecuteTranslator {

    /**
     * 生成根据主键更新数据的sql
     *
     * @param table       表名
     * @param primaryKey  主键属性
     * @param fieldValues pojo类属性数据列表
     * @return 更新数据的sql
     */
    String updateByPrimaryKey(String table, FieldValue primaryKey, List<FieldValue> fieldValues);


}
