package com.yishuifengxiao.common.security.httpsecurity;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>系统安全管理器</p>
 * <pre>
 *     HttpSecurity常用方法及说明
 * 由于HttpSecurity类中的方法实在是很多，所以挑其中比较重要的来说明下，遇到其他的请看官方文档或者自己上网搜索。
 *
 * 方法	说明
 * openidLogin()	用于基于 OpenId 的验证
 * headers()	将安全标头添加到响应
 * cors()	配置跨域资源共享（ CORS ）
 * sessionManagement()	允许配置会话管理
 * portMapper()	允许配置一个PortMapper(HttpSecurity#(getSharedObject(class)))，其他提供SecurityConfigurer的对象使用 PortMapper 从 HTTP 重定向到 HTTPS 或者从 HTTPS 重定向到 HTTP。默认情况下，Spring Security使用一个PortMapperImpl映射 HTTP 端口8080到 HTTPS 端口8443，HTTP 端口80到 HTTPS 端口443
 * jee()	配置基于容器的预认证。 在这种情况下，认证由Servlet容器管理
 * x509()	配置基于x509的认证
 * rememberMe	允许配置“记住我”的验证
 * authorizeRequests()	允许基于使用HttpServletRequest限制访问
 * requestCache()	允许配置请求缓存
 * exceptionHandling()	允许配置错误处理
 * securityContext()	在HttpServletRequests之间的SecurityContextHolder上设置SecurityContext的管理。 当使用WebSecurityConfigurerAdapter时，这将自动应用
 * servletApi()	将HttpServletRequest方法与在其上找到的值集成到SecurityContext中。 当使用WebSecurityConfigurerAdapter时，这将自动应用
 * csrf()	添加 CSRF 支持，使用WebSecurityConfigurerAdapter时，默认启用
 * logout()	添加退出登录支持。当使用WebSecurityConfigurerAdapter时，这将自动应用。默认情况是，访问URL”/ logout”，使HTTP Session无效来清除用户，清除已配置的任何#rememberMe()身份验证，清除SecurityContextHolder，然后重定向到”/login?success”
 * anonymous()	允许配置匿名用户的表示方法。 当与WebSecurityConfigurerAdapter结合使用时，这将自动应用。 默认情况下，匿名用户将使用org.springframework.security.authentication.AnonymousAuthenticationToken表示，并包含角色 “ROLE_ANONYMOUS”
 * formLogin()	指定支持基于表单的身份验证。如果未指定FormLoginConfigurer#loginPage(String)，则将生成默认登录页面
 * oauth2Login()	根据外部OAuth 2.0或OpenID Connect 1.0提供程序配置身份验证
 * requiresChannel()	配置通道安全。为了使该配置有用，必须提供至少一个到所需信道的映射
 * httpBasic()	配置 Http Basic 验证
 * addFilterAt()	在指定的Filter类的位置添加过滤器
 * </pre>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleHttpSecurityManager implements HttpSecurityManager, InitializingBean {
    /**
     * 系统中所有的Security 请求过滤器 实例
     */
    private List<AbstractSecurityRequestFilter> abstractSecurityRequestFilters;
    /**
     * 收集到所有的授权配置，Order的值越小，实例排在队列的越前面，这里需要使用有序队列
     */
    private List<HttpSecurityEnhanceCustomizer> httpSecurityEnhanceCustomizers;

    private AuthenticationPoint authenticationPoint;
    /**
     * 资源路径器
     */
    private SecurityPropertyResource securityPropertyResource;

    private UserDetailsService userDetailsService;


    @Override
    public void apply(HttpSecurity http) throws Exception {
        http.userDetailsService(this.userDetailsService);

//        RequestAttributeSecurityContextRepository 将 SecurityContext 保存为请求属性（request attribute），
//        以确保 SecurityContext 可用于跨调度（dispatch）类型发生的单个请求，这些调度类型可能会清除 SecurityContext。
//        例如，假设一个客户发出请求，经过验证，然后发生了错误。根据servlet容器的实现，
//        该错误意味着任何已建立的 SecurityContext 被清除，然后进行Error调度（dispatch）
//        。当进行Error调度时，没有建立任何 SecurityContext。这意味着Error页面不能使用 SecurityContext 进行授权或显示当前用户
//        ，除非 SecurityContext 以某种方式被持久化。
        // 在Spring Security 6中，下面的例子是默认配置。
        http.securityContext(httpSecuritySecurityContextConfigurer -> {
            httpSecuritySecurityContextConfigurer.securityContextRepository(new DelegatingSecurityContextRepository(
                    new RequestAttributeSecurityContextRepository(),
                    new HttpSessionSecurityContextRepository()
            )).requireExplicitSave(true);
        });

        if (null != this.abstractSecurityRequestFilters) {
            for (AbstractSecurityRequestFilter abstractSecurityRequestFilter : this.abstractSecurityRequestFilters) {
                if (securityPropertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( Security请求过滤器 ) 实例为 {}",
                            abstractSecurityRequestFilter);
                }
                abstractSecurityRequestFilter.configure(http);
            }
        }

        // 加入自定义的授权配置
        if (null != this.httpSecurityEnhanceCustomizers) {
            for (HttpSecurityEnhanceCustomizer authorizeConfigProvider : httpSecurityEnhanceCustomizers) {
                if (securityPropertyResource.showDetail()) {
                    log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( 授权提供器 ) 序号为 {} , 实例为 {}",
                            authorizeConfigProvider.order(), authorizeConfigProvider);
                }

                authorizeConfigProvider.apply(securityPropertyResource, authenticationPoint, http);

            }

        }


    }

    // @formatter:off
    public SimpleHttpSecurityManager(List<HttpSecurityEnhanceCustomizer> httpSecurityEnhanceCustomizers,
                                     SecurityPropertyResource securityPropertyResource,
                                     UserDetailsService userDetailsService,
                                     AuthenticationPoint authenticationPoint,
                                     List<AbstractSecurityRequestFilter> abstractSecurityRequestFilters) {

        this.httpSecurityEnhanceCustomizers = httpSecurityEnhanceCustomizers;
        this.securityPropertyResource = securityPropertyResource;
        this.authenticationPoint = authenticationPoint;
        this.abstractSecurityRequestFilters = abstractSecurityRequestFilters;
        this.userDetailsService=userDetailsService;
    }
    // @formatter:on

    @Override
    public void afterPropertiesSet() {
        this.httpSecurityEnhanceCustomizers =
                this.httpSecurityEnhanceCustomizers.stream().filter(Objects::nonNull).sorted(Comparator.comparing(HttpSecurityEnhanceCustomizer::order)).collect(Collectors.toList());
    }
}
