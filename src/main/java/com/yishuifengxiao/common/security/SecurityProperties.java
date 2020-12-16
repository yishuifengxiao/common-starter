/**
 * 
 */
package com.yishuifengxiao.common.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.security.constant.OAuth2Constant;
import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.constant.UriConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全相关的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.security")
public class SecurityProperties {
	/**
	 * 是否开启安全相关的功能
	 */
	private Boolean enable = true;

	/**
	 * 加解密中需要使用的密钥
	 */
	private String secretKey;
	/**
	 * 关闭csrf功能,默认为true
	 */
	private Boolean closeCsrf = true;
	/**
	 * 是否关闭cors保护，默认为false
	 */
	private Boolean closeCors = false;
	/**
	 * 是否开启httpBasic访问，默认为true
	 */
	private Boolean httpBasic = true;
	/**
	 * 资源名称,默认为yishuifengxiao
	 */
	private String realmName = OAuth2Constant.REAL_NAME;

	/**
	 * spring security 核心配置
	 */
	private CoreProperties core = new CoreProperties();
	/**
	 * spring security session相关的配置
	 */
	private SessionProperties session = new SessionProperties();

	/**
	 * spring security 忽视目录配置
	 */
	private IgnoreProperties ignore = new IgnoreProperties();
	/**
	 * 记住我相关的属性
	 */
	private RemeberMeProperties remeberMe = new RemeberMeProperties();
	/**
	 * 验证码及短信登陆相关配置
	 */
	private ValidateProperties code = new ValidateProperties();

	/**
	 * token生成相关的配置
	 */
	private TokenProperties token = new TokenProperties();
	/**
	 * 所有不经过资源授权管理的的资源路径<br/>
	 * key: 不参与解析，可以为任意值，但必须唯一<br/>
	 * value: 不希望经过授权管理的路径，采用Ant风格匹配,多个路径之间用半角逗号(,)分给开
	 */
	private Map<String, String> excludes = new HashMap<>();

	/**
	 * 所有直接放行的资源路径<br/>
	 * key: 不参与解析，可以为任意值，但必须唯一<br/>
	 * value: 不希望经过授权管理的路径，采用Ant风格匹配,多个路径之间用半角逗号(,)分给开
	 */
	private Map<String, String> permits = new HashMap<>();

	/**
	 * 所有需要自定义权限的资源路径<br/>
	 * key: 不参与解析，可以为任意值，但必须唯一<br/>
	 * value: 不希望经过授权管理的路径，采用Ant风格匹配,多个路径之间用半角逗号(,)分给开
	 */
	private Map<String, String> customs = new HashMap<>();

	/**
	 * 所有不需要经过权限校验的资源路径<br/>
	 * key: 不参与解析，可以为任意值，但必须唯一<br/>
	 * value: 不希望经过授权管理的路径，采用Ant风格匹配,多个路径之间用半角逗号(,)分给开
	 */
	private Map<String, String> unchecks = new HashMap<>();

	/**
	 * spring security 核心配置文件类
	 * 
	 * @version 0.0.1
	 * @author yishui
	 * @date 2018年6月29日
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CoreProperties {
		/**
		 * 表单提交时默认的用户名参数，默认值为 username
		 */
		private String usernameParameter = SecurityConstant.USERNAME_PARAMTER;
		/**
		 * 表单提交时默认的密码名参数，默认值为 password
		 */
		private String passwordParameter = SecurityConstant.PASSWORD_PARAMTER;
		/**
		 * 系统登陆页面的地址 ,默认为 /toLogin
		 */
		private String loginPage = UriConstant.DEFAULT_LOGIN_URL;
		/**
		 * 权限拦截时默认的跳转地址，默认为 /index
		 */
		private String redirectUrl = UriConstant.DEFAULT_REDIRECT_LOGIN_URL;
		/**
		 * 表单登陆时form表单请求的地址，默认为/login
		 */
		private String formActionUrl = UriConstant.DEFAULT_FORM_ACTION_URL;

		/**
		 * 默认的处理登出请求的URL的路径【即请求此URL即为退出操作】，默认为/logout
		 */
		private String loginOutUrl = UriConstant.DEFAULT_LOGINOUT_URL;

		/**
		 * 需要删除的cookie的名字 JSESSIONID
		 */
		private String cookieName = SecurityConstant.DEFAULT_COOKIE_NAME;
		
		/**
		 * 是否关闭前置参数验证,默认为false
		 */
		private Boolean closePreAuth=false;

	}

	/**
	 * spring security token生成配置文件类
	 * 
	 * @version 0.0.1
	 * @author yishui
	 * @date 2018年6月29日
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TokenProperties {

		/**
		 * 同一个账号最大的登陆数量。默认为 8888
		 */
		private Integer maxSessions = TokenConstant.MAX_SESSION_NUM;

		/**
		 * token的有效时间，单位为秒，默认的token有效时间，默认为24小时
		 */
		private Integer validSeconds = TokenConstant.TOKEN_VALID_TIME_IN_SECOND;

		/**
		 * 在达到同一个账号最大的登陆数量时是否阻止后面的用户登陆,默认为false
		 */
		private Boolean preventsLogin = false;
		/**
		 * 从请求头里取出认证信息时的参数名，默认为 xtoken
		 */
		private String headerParamter = TokenConstant.TOKEN_HEADER_PARAM;

		/**
		 * 从请求参数里取出认证信息时的参数名，默认为 yishui_token
		 */
		private String requestParamter = TokenConstant.TOKEN_REQUEST_PARAM;
		/**
		 * 用户唯一标识符参数，默认为user_unique_identitier
		 */
		private String userUniqueIdentitier = TokenConstant.USER_UNIQUE_IDENTIFIER;

	}

	/**
	 * spring security session相关的配置
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SessionProperties {
		/**
		 * 同一个用户在系统中的最大session数，默认8888
		 */
		private int maximumSessions = 8888;
		/**
		 * 达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
		 */
		private boolean maxSessionsPreventsLogin = false;
		/**
		 * session失效时跳转的地址
		 */
		private String sessionInvalidUrl = UriConstant.DEFAULT_SESSION_INVALID_URL;
	}

	/**
	 * spring security忽视目录
	 * 
	 * @author yishui
	 * @date 2019年1月8日
	 * @version 0.0.1
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class IgnoreProperties {

		/**
		 * 是否默认包含静态资源
		 */
		private Boolean containStaticResource = true;
		/**
		 * 是否包含swagger-ui的资源
		 */
		private Boolean containSwaagerUiResource = true;
		/**
		 * 是否包含actuator相关的路径
		 */
		private Boolean containActuator = true;
		/**
		 * 是否包含webJars资源
		 */
		private Boolean containWebjars = true;
		/**
		 * 是否包含所有的资源
		 */
		private Boolean containAll = false;

		/**
		 * 所有需要忽视的目录
		 */
		private Map<String, String> urls = new HashMap<>();

	}

	/**
	 * 记住我相关的属性配置
	 * 
	 * @author yishui
	 * @date 2019年1月8日
	 * @version 0.0.1
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RemeberMeProperties {
		/**
		 * 是否使用安全cookie
		 */
		private Boolean useSecureCookie = true;
		/**
		 * 记住我产生的token，默认为 yishuifengxiao
		 */
		private String key = SecurityConstant.REMEMBER_ME_AUTHENTICATION_KEY;
		/**
		 * 登陆时开启记住我的参数,默认为 rememberMe
		 */
		private String rememberMeParameter = SecurityConstant.REMEMBER_ME_PARAMTER;

		/**
		 * 默认过期时间为60分钟
		 */
		private Integer rememberMeSeconds = 60 * 60;

	}

	/**
	 * 短信验证码相关属性配置文件
	 * 
	 * @author yishui
	 * @date 2019年1月23日
	 * @version 0.0.1
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ValidateProperties {

		/**
		 * 是否过滤GET方法，默认为false
		 */
		private Boolean isFilterGet = false;
		/**
		 * 短信登陆参数,默认为 mobile
		 */
		private String smsLoginParam = SecurityConstant.SMS_LOGIN_PARAM;
		/**
		 * 短信验证码登录地址
		 */
		private String smsLoginUrl;
		/**
		 * 需要过滤的路径<br/>
		 * key：验证码类型的名字<br/>
		 * value: 需要过滤的路径，多个路径采用半角的逗号分隔
		 */
		private Map<String, String> filter = new HashMap<>();

	}

}
