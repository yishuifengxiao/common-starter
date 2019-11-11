/**
 * 
 */
package com.yishuifengxiao.common.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.constant.SecurityConstant;
import com.yishuifengxiao.common.security.eunm.HandleEnum;

/**
 * 安全相关的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
@ConfigurationProperties(prefix = "yishuifengxiao.security")
public class SecurityProperties {

	/**
	 * 自定义处理器中成功的处理方式
	 */
	private HandlerProperties handler = new HandlerProperties();
	/**
	 * spring security 核心配置
	 */
	private CoreProperties core = new CoreProperties();
	/**
	 * spring security session相关的配置
	 */
	private SessionProperties session = new SessionProperties();

	/**
	 * 需要自定义权限的路径
	 */
	private CustomAuthProperties custom = new CustomAuthProperties();

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
	 * 加解密中需要使用的密钥
	 */
	private String secretKey;
	/**
	 * 关闭csrf功能,默认为true
	 */
	private Boolean closeCsrf = SecurityConstant.CLOSE_CSRF;
	/**
	 * 是否关闭cors保护，默认为false
	 */
	private Boolean closeCors = SecurityConstant.CLOSE_CORS;
	/**
	 * 是否开启httpBasic访问，默认为true
	 */
	private Boolean httpBasic = SecurityConstant.HTTP_BASIC;
	/**
	 * 资源名称,默认为yishuifengxiao
	 */
	private String realmName = SecurityConstant.REAL_NAME;

	/**
	 * 自定义处理器中成功的处理方式
	 */
	public HandlerProperties getHandler() {
		return handler;
	}

	public void setHandler(HandlerProperties handler) {
		this.handler = handler;
	}

	/**
	 * spring security 核心配置
	 */
	public CoreProperties getCore() {
		return core;
	}

	public void setCore(CoreProperties core) {
		this.core = core;
	}

	/**
	 * spring security session相关的配置
	 */
	public SessionProperties getSession() {
		return session;
	}

	public void setSession(SessionProperties session) {
		this.session = session;
	}

	/**
	 * 需要自定义权限的路径
	 */
	public CustomAuthProperties getCustom() {
		return custom;
	}

	public void setCustom(CustomAuthProperties custom) {
		this.custom = custom;
	}

	/**
	 * spring security 忽视目录配置
	 */
	public IgnoreProperties getIgnore() {
		return ignore;
	}

	public void setIgnore(IgnoreProperties ignore) {
		this.ignore = ignore;
	}

	/**
	 * 加解密中需要使用的密钥
	 */
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * 关闭csrf功能,默认为true
	 */
	public Boolean getCloseCsrf() {
		return closeCsrf;
	}

	public void setCloseCsrf(Boolean closeCsrf) {
		this.closeCsrf = closeCsrf;
	}

	/**
	 * 是否开启httpBasic访问，默认为true
	 */
	public Boolean getHttpBasic() {
		return httpBasic;
	}

	public void setHttpBasic(Boolean httpBasic) {
		this.httpBasic = httpBasic;
	}

	/**
	 * 资源名称,默认为yishuifengxiao
	 */
	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	/**
	 * 是否关闭cors保护，默认为false
	 */
	public Boolean getCloseCors() {
		return closeCors;
	}

	public void setCloseCors(Boolean closeCors) {
		this.closeCors = closeCors;
	}

	/**
	 * 记住我相关的属性
	 * 
	 * @return
	 */
	public RemeberMeProperties getRemeberMe() {
		return remeberMe;
	}

	public void setRemeberMe(RemeberMeProperties remeberMe) {
		this.remeberMe = remeberMe;
	}

	/**
	 * 验证码及短信登陆相关配置
	 * 
	 * @return
	 */
	public ValidateProperties getCode() {
		return code;
	}

	public void setCode(ValidateProperties code) {
		this.code = code;
	}

	/**
	 * 自定义handler的配置文件
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class HandlerProperties {
		/**
		 * 登陆成功后的处理方式
		 */
		private LoginSucProperties suc = new LoginSucProperties();
		/**
		 * 登陆失败时的处理方式
		 */
		private LoginFailProperties fail = new LoginFailProperties();
		/**
		 * 退出成功后的配置
		 */
		private LoginOutProperties exit = new LoginOutProperties();

		/**
		 * 出现异常时的配置
		 */
		private ExceptionProperties exception = new ExceptionProperties();
		/**
		 * 权限拒绝时的配置
		 */
		private AccessDenieProperties denie = new AccessDenieProperties();
		/**
		 * 默认的header名称 type
		 */
		private String headerName = SecurityConstant.DEFAULT_HEADER_NAME;
		/**
		 * 默认的从请求参数获取处理方法的参数的名称 yishuifengxiao
		 */
		private String paramName = SecurityConstant.DEFAULT_PARAM_NAME;

		/**
		 * 登陆成功后的处理方式
		 */
		public LoginSucProperties getSuc() {
			return suc;
		}

		public void setSuc(LoginSucProperties suc) {
			this.suc = suc;
		}

		/**
		 * 登陆失败时的处理方式
		 */
		public LoginFailProperties getFail() {
			return fail;
		}

		public void setFail(LoginFailProperties fail) {
			this.fail = fail;
		}

		/**
		 * 退出成功后的配置
		 */
		public LoginOutProperties getExit() {
			return exit;
		}

		public void setExit(LoginOutProperties exit) {
			this.exit = exit;
		}

		/**
		 * 默认的header名称 type
		 */
		public String getHeaderName() {
			return headerName;
		}

		public void setHeaderName(String headerName) {
			this.headerName = headerName;
		}

		/**
		 * 出现异常时的配置
		 */
		public ExceptionProperties getException() {
			return exception;
		}

		public void setException(ExceptionProperties exception) {
			this.exception = exception;
		}

		/**
		 * 权限拒绝时的配置
		 */
		public AccessDenieProperties getDenie() {
			return denie;
		}

		public void setDenie(AccessDenieProperties denie) {
			this.denie = denie;
		}

		/**
		 * 默认的从请求参数获取处理方法的参数的名称 yishuifengxiao
		 * 
		 * @return
		 */
		public String getParamName() {
			return paramName;
		}

		public void setParamName(String paramName) {
			this.paramName = paramName;
		}

	}

	/**
	 * spring security 核心配置文件类
	 * 
	 * @version 0.0.1
	 * @author yishui
	 * @date 2018年6月29日
	 */
	public static class CoreProperties {
		/**
		 * 表单提交时默认的用户名参数
		 */
		private String usernameParameter = SecurityConstant.USERNAME_PARAMTER;
		/**
		 * 表单提交时默认的密码名参数
		 */
		private String passwordParameter = SecurityConstant.PASSWORD_PARAMTER;
		/**
		 * 系统登陆页面的地址 ,默认为 /login
		 */
		private String loginPage = SecurityConstant.DEFAULT_LOGIN_URL;
		/**
		 * 权限拦截时默认的跳转地址，默认为 /index
		 */
		private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;
		/**
		 * 表单登陆时form表单请求的地址，默认为/auth/form
		 */
		private String formActionUrl = SecurityConstant.DEFAULT_FORM_ACTION_URL;

		/**
		 * 默认的处理登出请求的URL的路径【即请求此URL即为退出操作】，默认为/loginOut
		 */
		private String loginOutUrl = SecurityConstant.DEFAULT_LOGINOUT_URL;

		/**
		 * 系统登陆页面的地址，默认为/login
		 */
		public String getLoginPage() {
			return loginPage;
		}

		public void setLoginPage(String loginPage) {
			this.loginPage = loginPage;
		}

		/**
		 * 权限拦截时默认的跳转地址，默认为/index
		 */
		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

		/**
		 * 表单登陆时form表单请求的地址，默认为/auth/form
		 */
		public String getFormActionUrl() {
			return formActionUrl;
		}

		public void setFormActionUrl(String formActionUrl) {
			this.formActionUrl = formActionUrl;
		}

		/**
		 * 默认的处理登出请求的URL的路径【即请求次URL即为退出操作】，默认为/login/loginOut
		 */
		public String getLoginOutUrl() {
			return loginOutUrl;
		}

		public void setLoginOutUrl(String loginOutUrl) {
			this.loginOutUrl = loginOutUrl;
		}

		/**
		 * 表单提交时默认的用户名参数
		 */
		public String getUsernameParameter() {
			return usernameParameter;
		}

		public void setUsernameParameter(String usernameParameter) {
			this.usernameParameter = usernameParameter;
		}

		/**
		 * 表单提交时默认的密码名参数
		 */
		public String getPasswordParameter() {
			return passwordParameter;
		}

		public void setPasswordParameter(String passwordParameter) {
			this.passwordParameter = passwordParameter;
		}

	}

	/**
	 * spring security session相关的配置
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class SessionProperties {
		/**
		 * 同一个用户在系统中的最大session数，默认1
		 */
		private int maximumSessions = 1;
		/**
		 * 达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
		 */
		private boolean maxSessionsPreventsLogin = false;
		/**
		 * session失效时跳转的地址
		 */
		private String sessionInvalidUrl = SecurityConstant.DEFAULT_SESSION_INVALID_URL;

		/**
		 * 同一个用户在系统中的最大session数，默认1
		 */
		public int getMaximumSessions() {
			return maximumSessions;
		}

		public void setMaximumSessions(int maximumSessions) {
			this.maximumSessions = maximumSessions;
		}

		/**
		 * 达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
		 */
		public boolean isMaxSessionsPreventsLogin() {
			return maxSessionsPreventsLogin;
		}

		public void setMaxSessionsPreventsLogin(boolean maxSessionsPreventsLogin) {
			this.maxSessionsPreventsLogin = maxSessionsPreventsLogin;
		}

		/**
		 * session失效时跳转的地址
		 */
		public String getSessionInvalidUrl() {
			return sessionInvalidUrl;
		}

		public void setSessionInvalidUrl(String sessionInvalidUrl) {
			this.sessionInvalidUrl = sessionInvalidUrl;
		}
	}

	/**
	 * 自定义权限的配置文件
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class CustomAuthProperties {

		/**
		 * 需要自定义授权的路径 <br/>
		 * 键 : 自定义，不参与匹配 <br/>
		 * 值 ：需要匹配的路径，多个路径之间用半角逗号(,)隔开
		 */
		private Map<String, String> map = new HashMap<>();

		/**
		 * 获取所有需要设置自定义权限的路径
		 * 
		 * @return
		 */
		public Map<String, String> getMap() {
			return map;
		}

		/**
		 * 设置所有需要设置自定义权限的路径
		 * 
		 * @return
		 */
		public void setMap(Map<String, String> map) {
			this.map = map;
		}

		/**
		 * 获取所有自定义权限的路径
		 * 
		 * @return
		 */
		public List<String> getAll() {
			List<String> list = new ArrayList<>();
			map.forEach((k, v) -> {
				if (StringUtils.isNotBlank(v)) {
					List<String> customs = Arrays.asList(v.split(",")).parallelStream()
							.filter(t -> StringUtils.isNotBlank(t)).map(t -> t.trim()).collect(Collectors.toList());
					if (customs != null && customs.size() > 0) {
						list.addAll(customs);
					}
				}

			});

			return list;
		}

	}

	/**
	 * spring security忽视目录
	 * 
	 * @author yishui
	 * @date 2019年1月8日
	 * @version 0.0.1
	 */
	public static class IgnoreProperties {
		/**
		 * 系统默认包含的静态路径
		 */
		private String[] staticResource = new String[] { "/js/**", "/css/**", "/images/**", "/fonts/**", "/**/**.png",
				"/**/**.jpg", "/**/**.html", "/**/**.ico", "/**/**.js", "/**/**.css", "/**/**.woff", "/**/**.ttf" };

		/**
		 * 系统默认包含的swagger-ui资源路径
		 */
		private String[] swaagerUiResource = new String[] { "/swagger-ui.html", "/swagger-resources/**",
				"/v2/api-docs" };
		/**
		 * 系统默认包含actuator相关的路径
		 */
		private String[] actuatorResource = new String[] { "/actuator/**" };
		/**
		 * 系统默认包含webjars相关的路径
		 */
		private String[] webjarsResource = new String[] { "/webjars/**" };
		/**
		 * 所有的资源
		 */
		private String[] allResources = new String[] { "/**" };

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
		private Map<String, String> map = new HashMap<>();

		/**
		 * 以字符串形式获取到所有需要忽略的路径
		 * 
		 * @return
		 */
		public String getIgnoreString() {
			return Arrays.asList(this.getIgnore()).parallelStream().filter(t -> StringUtils.isNotBlank(t))
					.collect(Collectors.joining(", "));

		}

		/**
		 * 获取所有需要忽视的目录
		 * 
		 * @return
		 */
		public String[] getIgnore() {
			Set<String> set = new HashSet<>();
			if (this.containStaticResource) {
				set.addAll(Arrays.asList(staticResource));
			}
			if (this.containSwaagerUiResource) {
				set.addAll(Arrays.asList(swaagerUiResource));
			}
			if (this.containActuator) {
				set.addAll(Arrays.asList(actuatorResource));
			}
			if (this.containWebjars) {
				set.addAll(Arrays.asList(webjarsResource));
			}
			if (this.containAll) {
				set.addAll(Arrays.asList(allResources));
			}
			map.forEach((k, v) -> {
				if (StringUtils.isNotBlank(v)) {
					List<String> ignores = Arrays.asList(v.split(",")).parallelStream()
							.filter(t -> StringUtils.isNotBlank(t)).map(t -> t.trim()).collect(Collectors.toList());
					set.addAll(ignores);
				}
			});
			return set.toArray(new String[] {});
		}

		/**
		 * 是否默认包含静态资源，默认为包含
		 */
		public Boolean getContainStaticResource() {
			return containStaticResource;
		}

		public void setContainStaticResource(Boolean containStaticResource) {
			this.containStaticResource = containStaticResource;
		}

		/**
		 * 所有需要忽视的目录
		 */
		public Map<String, String> getMap() {
			return map;
		}

		public void setMap(Map<String, String> map) {
			this.map = map;
		}

		public String[] getStaticResource() {
			return staticResource;
		}

		public void setStaticResource(String[] staticResource) {
			this.staticResource = staticResource;
		}

		public String[] getSwaagerUiResource() {
			return swaagerUiResource;
		}

		public void setSwaagerUiResource(String[] swaagerUiResource) {
			this.swaagerUiResource = swaagerUiResource;
		}

		public String[] getActuatorResource() {
			return actuatorResource;
		}

		public void setActuatorResource(String[] actuatorResource) {
			this.actuatorResource = actuatorResource;
		}

		public String[] getWebjarsResource() {
			return webjarsResource;
		}

		public void setWebjarsResource(String[] webjarsResource) {
			this.webjarsResource = webjarsResource;
		}

		public Boolean getContainSwaagerUiResource() {
			return containSwaagerUiResource;
		}

		public void setContainSwaagerUiResource(Boolean containSwaagerUiResource) {
			this.containSwaagerUiResource = containSwaagerUiResource;
		}

		public Boolean getContainActuator() {
			return containActuator;
		}

		public void setContainActuator(Boolean containActuator) {
			this.containActuator = containActuator;
		}

		public Boolean getContainWebjars() {
			return containWebjars;
		}

		public void setContainWebjars(Boolean containWebjars) {
			this.containWebjars = containWebjars;
		}

		public String[] getAllResources() {
			return allResources;
		}

		public void setAllResources(String[] allResources) {
			this.allResources = allResources;
		}

		public Boolean getContainAll() {
			return containAll;
		}

		public void setContainAll(Boolean containAll) {
			this.containAll = containAll;
		}

	}

	/**
	 * 记住我相关的属性配置
	 * 
	 * @author yishui
	 * @date 2019年1月8日
	 * @version 0.0.1
	 */
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

		public Boolean getUseSecureCookie() {
			return useSecureCookie;
		}

		public void setUseSecureCookie(Boolean useSecureCookie) {
			this.useSecureCookie = useSecureCookie;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getRememberMeParameter() {
			return rememberMeParameter;
		}

		public void setRememberMeParameter(String rememberMeParameter) {
			this.rememberMeParameter = rememberMeParameter;
		}

		/**
		 * 默认过期时间为60分钟
		 */
		public Integer getRememberMeSeconds() {
			return rememberMeSeconds;
		}

		public void setRememberMeSeconds(Integer rememberMeSeconds) {
			this.rememberMeSeconds = rememberMeSeconds;
		}

	}

	/**
	 * 短信验证码相关属性配置文件
	 * 
	 * @author yishui
	 * @date 2019年1月23日
	 * @version 0.0.1
	 */
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

		public Map<String, String> getFilter() {
			return filter;
		}

		public void setFilter(Map<String, String> filter) {
			this.filter = filter;
		}

		/**
		 * 短信验证码登录地址
		 * 
		 * @return
		 */
		public String getSmsLoginUrl() {
			return smsLoginUrl;
		}

		public void setSmsLoginUrl(String smsLoginUrl) {
			this.smsLoginUrl = smsLoginUrl;
		}

		/**
		 * 是否过滤GET方法，默认为false
		 * 
		 * @return
		 */
		public Boolean getIsFilterGet() {
			return isFilterGet;
		}

		public void setIsFilterGet(Boolean isFilterGet) {
			this.isFilterGet = isFilterGet;
		}

		/**
		 * 短信登陆参数,默认为 mobile
		 * 
		 * @return
		 */
		public String getSmsLoginParam() {
			return smsLoginParam;
		}

		public void setSmsLoginParam(String smsLoginParam) {
			this.smsLoginParam = smsLoginParam;
		}

	}

	

	/**
	 * 权限拒绝时的配置
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class AccessDenieProperties {
		/**
		 * 默认为内容协商
		 */
		private HandleEnum returnType = HandleEnum.AUTO;
		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面
		 */
		private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;

		/**
		 * 默认为重定向
		 */
		public HandleEnum getReturnType() {
			return returnType;
		}

		public void setReturnType(HandleEnum returnType) {
			this.returnType = returnType;
		}

		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面
		 */
		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

	}

	/**
	 * 全局异常转换时的数据
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class ExceptionProperties {
		/**
		 * 默认为内容协商
		 */
		private HandleEnum returnType = HandleEnum.AUTO;
		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面
		 */
		private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;

		/**
		 * 默认为重定向
		 */
		public HandleEnum getReturnType() {
			return returnType;
		}

		public void setReturnType(HandleEnum returnType) {
			this.returnType = returnType;
		}

		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面
		 */
		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

	}

	/**
	 * 登陆失败后的配置
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class LoginFailProperties {
		/**
		 * 默认为内容协商处理
		 */
		private HandleEnum returnType = HandleEnum.AUTO;
		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面 /index
		 */
		private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;

		/**
		 * 默认为内容协商处理
		 */
		public HandleEnum getReturnType() {
			return returnType;
		}

		public void setReturnType(HandleEnum returnType) {
			this.returnType = returnType;
		}

		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面 /index
		 */
		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

	}

	/**
	 * 退出成功后的配置
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class LoginOutProperties {
		/**
		 * 默认为重定向
		 */
		private HandleEnum returnType = HandleEnum.REDIRECT;
		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面 /index
		 */
		private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;

		/**
		 * 需要删除的cookie的名字 JSESSIONID
		 */
		private String cookieName = SecurityConstant.DEFAULT_COOKIE_NAME;

		/**
		 * 默认为重定向
		 */
		public HandleEnum getReturnType() {
			return returnType;
		}

		public void setReturnType(HandleEnum returnType) {
			this.returnType = returnType;
		}

		/**
		 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面 /index
		 */
		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

		/**
		 * 需要删除的cookie的名字 JSESSIONID
		 */
		public String getCookieName() {
			return cookieName;
		}

		public void setCookieName(String cookieName) {
			this.cookieName = cookieName;
		}

	}

	/**
	 * 登陆成功后的配置
	 * 
	 * @author yishui
	 * @date 2019年1月5日
	 * @version 0.0.1
	 */
	public static class LoginSucProperties {
		/**
		 * 默认为重定向
		 */
		private HandleEnum returnType = HandleEnum.AUTO;
		/**
		 * 默认的重定向URL，默认为主页地址 /
		 */
		private String redirectUrl = SecurityConstant.DEFAULT_HOME_URL;

		/**
		 * 默认为重定向
		 */
		public HandleEnum getReturnType() {
			return returnType;
		}

		public void setReturnType(HandleEnum returnType) {
			this.returnType = returnType;
		}

		/**
		 * 默认的重定向URL，默认为主页地址 /
		 */
		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

	}

}
