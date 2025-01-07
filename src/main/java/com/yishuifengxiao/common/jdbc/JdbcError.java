package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.tool.entity.RootEnum;

/**
 * 错误码
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/11/3 13:08
 * @since 1.0.0
 */
public enum JdbcError implements RootEnum {
    /**
     * 无数据库主键
     */
    NO_PRIMARY_KEY(6001, "无数据库主键"),
    /**
     * 数据库主键为空
     */
    NULL_PRIMARY_KEY(6002, "数据库主键为空"),
    /**
     * 删除条件为空
     */
    DELETE_PARAMS_IS_ALL_NULL(6003, "删除条件为空"),

    /**
     * 参数不能全为null
     */
    PARAMS_IS_ALL_NULL(6004, "参数不能全为null"),
    /**
     * 多个主键属性
     */
    MULTIPLE_PRIMARY_KEYS(6005, "多个主键属性");

    /**
     * 错误码
     */
    private int code;
    /**
     * 错误描述信息
     */
    private String message;

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误描述信息
     */
    JdbcError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误描述信息
     *
     * @return 错误描述信息
     */
    public String getMessage() {
        return message;
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String description() {
        return this.message;
    }
}
