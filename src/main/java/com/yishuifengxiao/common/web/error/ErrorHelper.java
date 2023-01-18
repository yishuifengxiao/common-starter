/**
 *
 */
package com.yishuifengxiao.common.web.error;

import com.yishuifengxiao.common.tool.entity.Response;

/**
 * 异常信息补充提取工具
 *
 * @author qingteng
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ErrorHelper {

    /**
     * 根据异常原因生成对应的响应
     *
     * @param e                   异常信息
     * @return 响应数据
     */
    Response extract(Throwable e);

}
