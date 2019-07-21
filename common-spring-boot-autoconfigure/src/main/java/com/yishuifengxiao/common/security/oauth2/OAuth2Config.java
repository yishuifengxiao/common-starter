package com.yishuifengxiao.common.security.oauth2;

import org.springframework.context.annotation.Configuration;

public class OAuth2Config {

	@Configuration
	public class OAuth2ResourceConfig extends Oauth2Resource {
	}

	@Configuration
	public class OAuth2ServerConfig extends Oauth2Server {
	}
}
