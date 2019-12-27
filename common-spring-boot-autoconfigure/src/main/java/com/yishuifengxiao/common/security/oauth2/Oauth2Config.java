package com.yishuifengxiao.common.security.oauth2;

import org.springframework.context.annotation.Configuration;

/**
 * oauth2的相关的配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class Oauth2Config {

	@Configuration
	public class Oauth2ResourceConfig extends Oauth2Resource {
	}

	@Configuration
	public class Oauth2ServerConfig extends Oauth2Server {
	}
}
