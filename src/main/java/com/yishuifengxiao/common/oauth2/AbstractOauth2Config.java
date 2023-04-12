package com.yishuifengxiao.common.oauth2;

import com.yishuifengxiao.common.oauth2.token.TokenStrategy;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityManager;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * oauth2的相关的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class AbstractOauth2Config {
    @Configuration
    @Primary
    public class Oauth2Resource extends Oauth2ResourceConfig {

    }

    @Configuration
    @Primary
    public class Oauth2Server extends Oauth2ServerConfig {

    }


    public static class Oauth2ResourceConfig extends ResourceServerConfigurerAdapter {


        @Autowired
        private Oauth2Properties oauth2Properties;

        /**
         * 定义在security-core包中
         */
        @Autowired
        private AuthenticationPoint authenticationPoint;

        @Autowired
        private DefaultWebSecurityExpressionHandler expressionHandler;

        @Autowired
        private TokenExtractor tokenExtractor;

        /**
         * token生成器，负责token的生成或获取
         */
        @Autowired
        private TokenStrategy tokenStrategy;

        /**
         * 安全授权配置管理器
         */
        @Autowired
        protected HttpSecurityManager httpSecurityManager;
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {

            resources.authenticationEntryPoint(authenticationPoint);

            // 自定义token信息提取器
            resources.tokenExtractor(tokenExtractor == null ? new BearerTokenExtractor() : tokenExtractor);

            // 权限拒绝处理器
            resources.accessDeniedHandler(authenticationPoint);
            resources.stateless(false);

            // 不然自定义权限表达式不生效
            resources.expressionHandler(expressionHandler);
            resources.resourceId(oauth2Properties.getRealm());

            // token的验证和读取策略
            resources.tokenServices(tokenStrategy);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            httpSecurityManager.apply(http);
        }
    }


    public static class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private Oauth2Properties oauth2Properties;

        /**
         * 定义在security-core包中
         */
        @Autowired
        private AuthenticationPoint authenticationPoint;


        @Autowired
        private TokenStore tokenStore;

        /**
         * 授权管理器，在spring security里注入的
         */
        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        /**
         * 决定是否授权【具体定义参见Oauth2Config】
         */
        @Autowired
        private UserApprovalHandler userApprovalHandler;

        @Autowired
        private TokenEnhancer customeTokenEnhancer;

        @Autowired
        @Qualifier("authWebResponseExceptionTranslator")
        private WebResponseExceptionTranslator<OAuth2Exception> authWebResponseExceptionTranslator;
        /**
         * 决定是否授权
         */
        @Autowired
        @Qualifier("customClientDetailsService")
        private ClientDetailsService clientDetailsService;

        @Autowired
        @Qualifier("tokenEndpointAuthenticationFilter")
        private Filter tokenEndpointFilter;

        @Autowired
        private UserDetailsService userDetailsService;


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // @formatter:off
			endpoints
			    .userApprovalHandler(userApprovalHandler)
				.tokenStore(tokenStore)
				.authenticationManager(authenticationManager);
			
			// 增强器链
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
			tokenEnhancers.add(customeTokenEnhancer);
			tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
			
			//加入到增强器链中
			endpoints
				.tokenEnhancer(tokenEnhancerChain);
			
			
			//配置token的生成规则
//	      endpoints.tokenServices(tokenStrategy);
			
	      //防止刷新token时报错  UserDetailsService is required. 
	      endpoints.userDetailsService(userDetailsService);
	      
	      endpoints.exceptionTranslator(authWebResponseExceptionTranslator);
			// @formatter:on
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            if (oauth2Properties.getCheckTokenAccess() != null) {
                security.checkTokenAccess(oauth2Properties.getCheckTokenAccess());
            }
            if (oauth2Properties.getTokenKeyAccess() != null) {
                security.tokenKeyAccess(oauth2Properties.getTokenKeyAccess());
            }
            if (oauth2Properties.getRealm() != null) {
                security.realm(oauth2Properties.getRealm());
            }
            security.authenticationEntryPoint(authenticationPoint);
            // Adds a new custom authentication filter for the TokenEndpoint.
            security.addTokenEndpointAuthenticationFilter(tokenEndpointFilter);

            security.allowFormAuthenticationForClients();
        }
    }

}
