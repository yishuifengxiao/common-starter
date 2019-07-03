package com.yishuifengxiao.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.yishuifengxiao.common.properties.CorsProperties;
import com.yishuifengxiao.common.properties.SwaggerProperties;

/**
 * 全局配置
 * 
 * @author yishui
 * @version 1.0.0
 * @date 2019-07-03
 */
@Configuration("common_starter_config")
@EnableConfigurationProperties({ SwaggerProperties.class, CorsProperties.class })
public class Config {

}
