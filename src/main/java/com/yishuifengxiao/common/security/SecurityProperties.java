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
import com.yishuifengxiao.common.tool.entity.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全属性配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
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
	 * 是否显示加载日志，默认为false
	 */
	private Boolean showDeatil = false;

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
	 * 提示信息
	 */
	private MessageProperties msg = new MessageProperties();
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
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
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
		 * 表单登陆时form表单请求的地址，默认为/web/login
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
		private Boolean closePreAuth = false;

	}

	/**
	 * spring security token生成配置文件类
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
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

		/**
		 * 是否在使用用户唯一标识符参数获取参数失败时使用请求的sessionId作为用户唯一标识符
		 */
		private Boolean useSessionId = false;

	}

	/**
	 * spring security session相关的配置
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
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
	 * @version 1.0.0
	 * @since 1.0.0
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
		 * 是否包含错误页面相关的路径，默认为包含
		 */
		private Boolean containErrorPage = true;
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
	 * @version 1.0.0
	 * @since 1.0.0
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
	 * @version 1.0.0
	 * @since 1.0.0
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
		 * <p>
		 * 需要过滤的路径
		 * </p>
		 * key：验证码类型的名字, value: 需要过滤的路径，多个路径采用半角的逗号分隔
		 */
		private Map<String, String> filter = new HashMap<>();

	}

	/**
	 * 短信验证码相关属性配置文件
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MessageProperties {

		/**
		 * 根据令牌值从系统中获取令牌的结果位null时的提示信息,默认值为 令牌无效或登陆状态已过期
		 */
		private String tokenIsNull = "令牌无效或登陆状态已过期";

		/**
		 * 根据令牌值从系统中获取令牌的已过期时的提示信息,默认值为 令牌已过期
		 */
		private String tokenIsExpired = "令牌已过期";

		/**
		 * 根据令牌值从系统中获取令牌的已失效时的提示信息,默认值为 令牌已失效
		 */
		private String tokenIsInvalid = "令牌已失效";

		/**
		 * 登录账号不存在时的提示信息,默认值为 账号不存在
		 */
		private String accountNoExtis = "账号不存在";

		/**
		 * 登录账号已过期时的提示信息,默认值为 账号已过期
		 */
		private String accountExpired = "账号已过期";

		/**
		 * 登录账号已锁定时的提示信息,默认值为 账号已锁定
		 */
		private String accountLocked = "账号已锁定";

		/**
		 * 登录账号对应的密码已过期时的提示信息,默认值为 密码已过期
		 */
		private String passwordExpired = "密码已过期";

		/**
		 * 登录账号未启用时的提示信息,默认值为 账号未启用
		 */
		private String accountNoEnable = "账号未启用";

		/**
		 * 登录密码错误时的提示信息，默认值为密码错误
		 */
		private String passwordIsError = "密码错误";

		/**
		 * 用户认证信息为null时的提示信息，默认为值 用户认证信息不能为空
		 */
		private String userDetailsIsNull = "用户认证信息不能为空";
		/**
		 * 本身是一个合法的用户，但是对于部分资源没有访问权限,访问这些资源时被拒绝时的提示信息，默认为
		 */
		private String accessIsDenied = "无权访问此资源";

		/**
		 * 本身是一个合法的用户，但是对于部分资源没有访问权限,访问这些资源时被拒绝时的响应码，默认值为 403
		 */
		private Integer accessDeniedCode = Response.Const.CODE_FORBIDDEN;

		/**
		 * 访问资源时因为权限等原因发生了异常后的处理(可能本身就不是一个合法的用户)时的提示信息，默认为该资源需要经过授权才能被访问
		 */
		private String visitOnError = "该资源需要经过授权才能被访问";

		/**
		 * 访问资源时因为权限等原因发生了异常后的处理(可能本身就不是一个合法的用户)时的响应码，默认值为401
		 */
		private Integer visitOnErrorCode = Response.Const.CODE_UNAUTHORIZED;

		/**
		 * 请求中未携带访问令牌或获取到的访问令牌为空时的提示信息，令牌不能为空
		 */
		private String tokenValueIsNull = "令牌不能为空";

		/**
		 * 请求中携带的访问令牌是非法或无效时的响应码，默认为 401
		 */
		private Integer invalidTokenValueCode = Response.Const.CODE_UNAUTHORIZED;

		/**
		 * 无效的登陆参数(用户名或密码不正确时的响应码),默认为 500
		 */
		private Integer invalidLoginParamCode = Response.Const.CODE_INTERNAL_SERVER_ERROR;
	}

}
