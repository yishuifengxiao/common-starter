/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.provider.processor;

import com.yishuifengxiao.common.security.constant.OAuth2Constant;
import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.support.HttpHelper;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>抽象协助处理器</p>
 *
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public abstract class BaseHandlerProcessor implements HandlerProcessor {

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
    public void login(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication, SecurityToken token) throws IOException {
        log.trace("【yishuifengxiao-common-spring-boot-starter】==============》 登陆成功,登陆的用户信息为 {}", token);


        String historyUrl = match(request);

        if (StringUtils.isNotBlank(historyUrl)) {
            // 如果是 /oauth2/authorize 请求，就直接跳转
            redirectStrategy.sendRedirect(request, response, historyUrl);
            return;
        }

        if (HttpHelper.isJsonRequest(request)) {
            HttpUtils.out(response, Response.sucData(token).setMsg("登陆成功"));
            return;
        }
        HttpUtils.redirect(request, response, propertyResource.security().getCore().getLoginSuccessUrl(), token);

    }

    /**
     * 登陆失败后的处理
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        失败的原因
     * @throws IOException 处理时发生问题
     */
    @Override
    public void failure(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {

        log.trace("【yishuifengxiao-common-spring-boot-starter】登录失败，失败的原因为 {}", exception.getMessage());


        String msg = "登陆失败";

        if (exception instanceof CustomException) {
            CustomException e = (CustomException) exception;
            msg = e.getMessage();
        }
        if (HttpHelper.isJsonRequest(request)) {
            HttpUtils.out(response, Response.of(propertyResource.security().getMsg().getInvalidLoginParamCode(), msg, exception.getMessage()));
            return;
        }
        HttpUtils.redirect(request, response, propertyResource.security().getCore().getLoginFailUrl(), exception);
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
    public void exit(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.trace("【yishuifengxiao-common-spring-boot-starter】退出成功，此用户的信息为 {}", authentication);


        if (HttpHelper.isJsonRequest(request)) {
            HttpUtils.out(response, Response.suc(authentication).setMsg("退出成功"));
            return;
        }
        HttpUtils.redirect(request, response, propertyResource.security().getCore().getLoginOutUrl(), authentication);
    }

    /**
     * <p>
     * 访问资源时权限被拒绝
     * </p>
     * 本身是一个合法的用户，但是对于部分资源没有访问权限
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        被拒绝的原因
     * @throws IOException 处理时发生问题
     */
    @Override
    public void deney(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {

        // 引起跳转的uri
        log.trace("【yishuifengxiao-common-spring-boot-starter】获取资源权限被拒绝,该资源的url为 {} , 失败的原因为 {}", this.getReferer(request, response), exception);

        if (HttpHelper.isJsonRequest(request)) {
            HttpUtils.out(response, Response.of(propertyResource.security().getMsg().getAccessDeniedCode(), propertyResource.security().getMsg().getAccessIsDenied(), exception.getMessage()));
            return;
        }
        HttpUtils.redirect(request, response, propertyResource.security().getCore().getRedirectUrl(), exception);
    }

    /**
     * <p>
     * 访问资源时因为权限等原因发生了异常后的处理
     * </p>
     * 可能本身就不是一个合法的用户
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        发生异常的原因
     * @throws IOException 处理时发生问题
     */
    @Override
    public void exception(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {


        log.trace("【yishuifengxiao-common-spring-boot-starter】获取资源 失败(可能是缺少token),该资源的url为 {} ,失败的原因为 {}", this.getReferer(request, response), exception);

        if (HttpHelper.isJsonRequest(request)) {
            HttpUtils.out(response, Response.of(propertyResource.security().getMsg().getVisitOnErrorCode(), propertyResource.security().getMsg().getVisitOnError(), exception));
            return;
        }
        HttpUtils.redirect(request, response, propertyResource.security().getCore().getRedirectUrl(), exception);

    }

    /**
     * <p>
     * 输出前置校验时出现的异常信息
     * </p>
     * 在进行前置校验时出现了问题，一般情况下为用户名或密码错误之类的
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param data     响应信息
     * @throws IOException 处理时发生问题
     */
    @Override
    public void preAuth(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Response<CustomException> data) throws IOException {
        log.trace("【yishuifengxiao-common-spring-boot-starter】==============》 自定义权限检查时发现问题 {}", data);


        if (HttpHelper.isJsonRequest(request)) {
            HttpUtils.out(response, data);
            return;
        }
        HttpUtils.redirect(request, response, propertyResource.security().getCore().getRedirectUrl(), data.getData());

    }

    /**
     * 从请求中获取请求的来源地址
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 请求的来源地址
     */
    protected String getReferer(HttpServletRequest request, HttpServletResponse response) {
        // 引起跳转的uri
        String redirectUrl = null;
        SavedRequest savedRequest = cache.getRequest(request, response);
        if (null != savedRequest) {
            redirectUrl = savedRequest.getRedirectUrl();
        }
        // 请求地址
        String requestUrl = request.getRequestURL().toString();

        request.getSession().setAttribute(SecurityConstant.HISTORY_REDIRECT_URL, redirectUrl);
        request.getSession().setAttribute(SecurityConstant.HISTORY_REQUEST_URL, requestUrl);

        return StringUtils.isNotBlank(redirectUrl) ? redirectUrl : requestUrl;
    }

    /**
     * 判断当前请求是否时符合跳转要求
     *
     * @param request HttpServletRequest
     * @return 如果匹配就返回为true, 否则为false
     */
    protected String match(HttpServletRequest request) {

        String historyUrl = (String) request.getSession().getAttribute(SecurityConstant.HISTORY_REDIRECT_URL);
        if (matcher.match(OAuth2Constant.AUTHORIZE_URL, historyUrl)) {
            return historyUrl;
        }

        historyUrl = (String) request.getSession().getAttribute(SecurityConstant.HISTORY_REQUEST_URL);
        if (matcher.match(OAuth2Constant.AUTHORIZE_URL, historyUrl)) {
            return historyUrl;
        }
        return null;
    }

}
