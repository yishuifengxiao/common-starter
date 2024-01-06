package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * http basic登陆时的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpBasicAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
		// 开启http baisc认证
		if (propertyResource.security().getHttpBasic()) {
			http.httpBasic() // 开启basic认证
					.authenticationEntryPoint(authenticationPoint)
					.realmName(propertyResource.security().getRealmName());
		}
		//@formatter:on  
    }

    @Override
    public int order() {
        return 700;
    }


}
