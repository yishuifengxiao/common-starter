package com.yishuifengxiao.common.jdbc.translator;

import java.util.List;

/**
 * <p>
 * 删除动作解释器
 * </p>
 * 负责执行删除相关的操作
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DeleteTranslator extends ExecuteTranslator {


    /**
     * 根据条件生成根据主键批量删除数据的sql
     *
     * @param table      表名
     * @param primaryKey 主键名称
     * @param values     主键值
     * @return 根据主键批量删除数据的sql
     */
    String deleteByPrimaryKeys(String table, String primaryKey, List<Object> values);


}
