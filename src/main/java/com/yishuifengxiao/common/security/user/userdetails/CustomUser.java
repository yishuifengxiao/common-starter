package com.yishuifengxiao.common.security.user.userdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息示例类
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/7 0:01
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUser implements Serializable {

    private String id;

    private String username;


}
