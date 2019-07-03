package com.yishuifengxiao.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.yishuifengxiao.common.properties.CorsProperties;
import com.yishuifengxiao.common.properties.SwaggerProperties;

@Configuration("common_starter_config")
@EnableConfigurationProperties({ SwaggerProperties.class, CorsProperties.class })
public class Config {

}
