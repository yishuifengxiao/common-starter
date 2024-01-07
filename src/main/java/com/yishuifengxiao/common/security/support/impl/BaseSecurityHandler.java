/**
 *
 */
package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.exception.ExpireTokenException;
import com.yishuifengxiao.common.security.exception.IllegalTokenException;
import com.yishuifengxiao.common.security.exception.InvalidTokenException;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.SecurityEvent;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.support.Strategy;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>抽象协助处理器</p>
 * <p>
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class BaseSecurityHandler implements SecurityHandler {

    /**
     * 声明了缓存与恢复操作
     */
    protected final RequestCache cache = new HttpSessionRequestCache();

    /**
     * 路径匹配策略
     */
    protected final AntPathMatcher matcher = new AntPathMatcher();
    /**
     * 重定向策略
     */
    protected final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * 登陆成功后的处理
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication 认证信息
     * @param token          生成的token
     * @throws IOException 处理时发生问题
     */
    @Override
    public void whenAuthenticationSuccess(SecurityPropertyResource securityPropertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication, SecurityToken token) throws IOException {
        log.trace("【yishuifengxiao-common-spring-boot-starter】==============》 登陆成功,登陆的用户信息为 {}", token);
        preHandle(request, response, securityPropertyResource, Strategy.AUTHENTICATION_SUCCESS, authentication, null);

        // 发布事件
        SpringContext.publishEvent(new SecurityEvent(this, request, response, securityPropertyResource, Strategy.AUTHENTICATION_SUCCESS, token, null));


        String redirectUrl = redirectUrl(request, response);

        if (StringUtils.isNotBlank(redirectUrl)) {
            log.info("【yishuifengxiao-common-spring-boot-starter】==============》 Login succeeded. It is detected " + "that" + " the historical blocking path is {}, and will be redirected to this address", redirectUrl);
            String requestParameter = securityPropertyResource.security().getToken().getRequestParameter();
            redirectUrl = UriComponentsBuilder.fromUriString(redirectUrl).replaceQueryParam(requestParameter, token.getValue()).build(true).toUriString();
            redirectStrategy.sendRedirect(request, response, redirectUrl);
            return;
        }
        if (isJsonRequest(request, response)) {
            sendJson(request, response, Strategy.AUTHENTICATION_SUCCESS, Response.sucData(token).setMsg("认证成功"));
            return;
        }
        redirect(request, response, Strategy.AUTHENTICATION_SUCCESS, securityPropertyResource.contextPath() + securityPropertyResource.security().getLoginSuccessUrl(), null, token);

    }

    /**
     * 登陆失败后的处理
     *
     * @param securityPropertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        失败的原因
     * @throws IOException 处理时发生问题
     */
    @Override
    public void whenAuthenticationFailure(SecurityPropertyResource securityPropertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {

        log.trace("【yishuifengxiao-common-spring-boot-starter】登录失败，失败的原因为 {}", exception.getMessage());

        preHandle(request, response, securityPropertyResource, Strategy.AUTHENTICATION_FAILURE, SecurityContextHolder.getContext().getAuthentication(), exception);
        // 发布事件
        SpringContext.publishEvent(new SecurityEvent(this, request, response, securityPropertyResource, Strategy.AUTHENTICATION_FAILURE, null, exception));
        String msg = "认证失败";

        if (exception instanceof CustomException) {
            CustomException e = (CustomException) exception;
            msg = e.getMessage();
        } else if (exception instanceof BadCredentialsException) {
            msg = securityPropertyResource.security().getMsg().getPasswordIsError();
        } else if (exception instanceof UsernameNotFoundException) {
            msg = securityPropertyResource.security().getMsg().getAccountNoExtis();
        } else if (exception instanceof LockedException) {
            msg = securityPropertyResource.security().getMsg().getAccountLocked();
        } else if (exception instanceof DisabledException) {
            msg = securityPropertyResource.security().getMsg().getAccountNoEnable();
        } else if (exception instanceof AccountExpiredException) {
            msg = securityPropertyResource.security().getMsg().getAccountExpired();
        } else if (exception instanceof CredentialsExpiredException) {
            msg = securityPropertyResource.security().getMsg().getPasswordExpired();
        }

        if (isJsonRequest(request, response)) {
            sendJson(request, response, Strategy.AUTHENTICATION_FAILURE, Response.of(securityPropertyResource.security().getMsg().getInvalidLoginParamCode(), msg, exception.getMessage()));
            return;
        }
        redirect(request, response, Strategy.AUTHENTICATION_FAILURE, securityPropertyResource.contextPath() + securityPropertyResource.security().getLoginFailUrl(), msg, exception);
    }

    /**
     * 退出成功后的处理
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication 认证信息
     * @throws IOException 处理时发生问题
     */
    @Override
    public void whenLogoutSuccess(SecurityPropertyResource securityPropertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.trace("【yishuifengxiao-common-spring-boot-starter】退出成功，此用户的信息为 {}", authentication);

        preHandle(request, response, securityPropertyResource, Strategy.LOGOUT_SUCCESS, authentication, null);

        // 发布事件
        SpringContext.publishEvent(new SecurityEvent(this, request, response, securityPropertyResource, Strategy.LOGOUT_SUCCESS, authentication, null));

        if (isJsonRequest(request, response)) {
            sendJson(request, response, Strategy.LOGOUT_SUCCESS, Response.suc(authentication).setMsg("退出成功"));
            return;
        }
        redirect(request, response, Strategy.LOGOUT_SUCCESS, securityPropertyResource.contextPath() + securityPropertyResource.security().getLoginOutUrl(), null, authentication);
    }

    /**
     * <p>
     * 访问资源时权限被拒绝
     * </p>
     * 本身是一个合法的用户，但是对于部分资源没有访问权限
     *
     * @param securityPropertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        被拒绝的原因
     * @throws IOException 处理时发生问题
     */
    @Override
    public void whenAccessDenied(SecurityPropertyResource securityPropertyResource, HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {

        // 引起跳转的uri
        log.trace("【yishuifengxiao-common-spring-boot-starter】获取资源权限被拒绝,该资源的url为 {} , 失败的原因为 {}", request.getRequestURL(), exception);
        preHandle(request, response, securityPropertyResource, Strategy.ACCESS_DENIED, SecurityContextHolder.getContext().getAuthentication(), exception);
        saveReferer(request, response);

        // 发布事件
        SpringContext.publishEvent(new SecurityEvent(this, request, response, securityPropertyResource, Strategy.ACCESS_DENIED, SecurityContextHolder.getContext().getAuthentication(), exception));

        if (isJsonRequest(request, response)) {
            if (exception instanceof InvalidTokenException || exception instanceof IllegalTokenException || exception instanceof ExpireTokenException) {
                sendJson(request, response, Strategy.ACCESS_DENIED, Response.of(securityPropertyResource.security().getMsg().getInvalidTokenValueCode(), securityPropertyResource.security().getMsg().getTokenIsNull(), exception.getMessage()));
            } else {
                sendJson(request, response, Strategy.ACCESS_DENIED, Response.of(securityPropertyResource.security().getMsg().getAccessDeniedCode(), securityPropertyResource.security().getMsg().getAccessIsDenied(), exception.getMessage()));
            }
            return;
        }
        redirect(request, response, Strategy.ACCESS_DENIED, securityPropertyResource.contextPath() + securityPropertyResource.security().getRedirectUrl(), null, exception);
    }

    /**
     * <p>
     * 访问资源时因为权限等原因发生了异常后的处理
     * </p>
     * 可能本身就不是一个合法的用户
     *
     * @param securityPropertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        发生异常的原因
     * @throws IOException 处理时发生问题
     */
    @Override
    public void onException(SecurityPropertyResource securityPropertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {


        log.trace("【yishuifengxiao-common-spring-boot-starter】获取资源 失败(可能是缺少token),该资源的url为 {} ,失败的原因为 {}", request.getRequestURL(), exception);
        preHandle(request, response, securityPropertyResource, Strategy.ON_EXCEPTION, SecurityContextHolder.getContext().getAuthentication(), exception);
        saveReferer(request, response);

        // 发布事件
        SpringContext.publishEvent(new SecurityEvent(this, request, response, securityPropertyResource, Strategy.ON_EXCEPTION, SecurityContextHolder.getContext().getAuthentication(), exception));

        if (isJsonRequest(request, response)) {
            sendJson(request, response, Strategy.ON_EXCEPTION, Response.of(securityPropertyResource.security().getMsg().getVisitOnErrorCode(), securityPropertyResource.security().getMsg().getVisitOnError(), exception));
            return;
        }
        redirect(request, response, Strategy.ON_EXCEPTION, securityPropertyResource.contextPath() + securityPropertyResource.security().getRedirectUrl(), null, exception);

    }


    /**
     * 从请求中获取请求的来源地址,在授权失败和拒绝时进行存储
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 请求的来源地址
     */
    protected void saveReferer(HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.equalsIgnoreCase(request.getMethod(), HttpMethod.GET.name())) {
            // 不是get请求，放弃
            return;
        }
        if (isJsonRequest(request, response)) {
            // json 请求，放弃
            return;
        }
        try {
            // 引起跳转的uri
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (null != parameterMap) {
                parameterMap.forEach((k, v) -> {
                    uriBuilder.queryParam(k, v);
                });
            }
            // build(true) -> Components are explicitly encoded
            String redirectUrl = uriBuilder.build(true).toUriString();
            request.getSession().setAttribute(SecurityConstant.HISTORY_REQUEST_URL, redirectUrl);
        } catch (Exception e) {
        }

    }

    /**
     * 当前请求是否为json请求
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return true表示当前请求是为json请求
     */
    protected boolean isJsonRequest(HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.equals(request.getParameter("__response_strategy"), "redirect")) {
            return false;
        }
        return HttpUtils.isJsonRequest(request);
    }

    /**
     * 前置处理
     *
     * @param request
     * @param response
     * @param securityPropertyResource
     * @param strategy
     * @param authentication
     * @param exception
     */
    protected void preHandle(HttpServletRequest request, HttpServletResponse response, SecurityPropertyResource securityPropertyResource, Strategy strategy, Authentication authentication, Exception exception) {
    }

    /**
     * 重定向到指定的地址
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param strategy 处理类型
     * @param url      重定向地址
     * @param msg      异常提示信息
     * @param data     附带信息
     * @throws IOException
     */
    protected void redirect(HttpServletRequest request, HttpServletResponse response, Strategy strategy, String url, String msg, Object data) throws IOException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (StringUtils.isNotBlank(msg)) {
            uriBuilder.queryParam("error_msg", URLEncoder.encode(msg, StandardCharsets.UTF_8.name()));
        } else {
            if (null != data) {
                if (data instanceof Throwable) {
                    if (!(data instanceof NullPointerException)) {
                        uriBuilder.queryParam("error_msg", URLEncoder.encode(((Throwable) data).getMessage(), StandardCharsets.UTF_8.name()));
                    }
                } else {
                    uriBuilder.queryParam("error_msg", URLEncoder.encode(JsonUtil.toJSONString(data), StandardCharsets.UTF_8.name()));
                }
            }
        }


        url = uriBuilder.build(true).toUriString();
        HttpUtils.redirect(request, response, url, data);
    }

    /**
     * 发送json格式的响应数据
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param strategy 处理类型
     * @param data     附带信息
     */
    protected void sendJson(HttpServletRequest request, HttpServletResponse response, Strategy strategy, Object data) {
        HttpUtils.write(request, response, data);
    }

    /**
     * 获取重定向地址
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 重定向地址
     */
    protected String redirectUrl(HttpServletRequest request, HttpServletResponse response) {
        Object url = request.getSession().getAttribute(SecurityConstant.HISTORY_REQUEST_URL);
        request.getSession().setAttribute(SecurityConstant.HISTORY_REQUEST_URL, "");
        if (null != url && StringUtils.isNotBlank(url.toString()) && !StringUtils.equalsIgnoreCase(url.toString(), "null")) {
            return url.toString();
        }
        return null;
    }

}
