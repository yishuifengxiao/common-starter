/**
 * 
 */
package com.yishuifengxiao.common.security.adapter;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * security 自定义适配器，自定义过滤器需要继承的基类<br/>
 * 参见 com.yishuifengxiao.common.security.code.adater.CodeConfigAdapter
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version v1.0.0
 */
public abstract class AbstractSecurityAdapter
		extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

}
