package com.yishuifengxiao.common.jdbc;

import java.util.Map;

/**
 * 命名参数处理器接口
 * <p>
 * 实现此接口的类负责根据传入的参数映射生成SQL语句和参数。
 * </p>
 *
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public interface NamedHandler {
    /**
     * 处理命名参数，生成SQL语句和参数映射
     *
     * @param params 包含命名参数的映射，键为参数名，值为参数值
     * @return 处理后的SQL语句
     */
    String handle(final Map<String, Object> params);
}
