package com.yishuifengxiao.common.jdbc.translator;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;

import java.util.List;

/**
 * <p>
 * 插入动作解释器
 * </p>
 * 负责执行插入相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface InsertTranslator extends ExecuteTranslator {


    /**
     * 生成数据插入的sql语句
     *
     * @param table       表名
     * @param fieldValues pojo类属性数据列表
     * @return
     */
    String insert(String table, List<FieldValue> fieldValues);

}
