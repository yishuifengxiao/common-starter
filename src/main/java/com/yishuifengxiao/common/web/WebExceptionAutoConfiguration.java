package com.yishuifengxiao.common.web;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;

import com.yishuifengxiao.common.support.TraceContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.web.error.ExceptionHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常捕获自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@ControllerAdvice
@ResponseBody
@ConditionalOnProperty(prefix = "yishuifengxiao.error", name = {"enable"}, havingValue = "true", matchIfMissing = true)
@Priority(1)
public class WebExceptionAutoConfiguration {
    @Autowired
    private WebFilterProperties webProperties;
    @Autowired
    private ExceptionHelper exceptionHelper;

    /**
     * 500 - 自定义异常
     *
     * @param request HttpServletRequest
     * @param e       希望捕获的异常
     * @return 发生异常捕获之后的响应
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({CustomException.class, UncheckedException.class})
    public Object catchCustomException(HttpServletRequest request, Exception e) {
        String ssid = this.getRequestId(request);
        String uri = null != request ? request.getRequestURI() : null;
        Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).setId(ssid);
        if (log.isInfoEnabled()) {
            log.info("【Global exception interception】【 CustomException 】(Custom exception) traceId={} request {} request failed, custom exception {} was intercepted", ssid, uri, e);
        }

        return response;
    }

    /**
     * 500 - Internal Server Error
     *
     * @param request HttpServletRequest
     * @param e       希望捕获的异常
     * @return 发生异常捕获之后的响应
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({RuntimeException.class, Exception.class, Throwable.class})
    public Object catchThrowable(HttpServletRequest request, Throwable e) {
        String ssid = this.getRequestId(request);
        String uri = null != request ? request.getRequestURI() : null;
        Response<Object> response = exceptionHelper.extract(e, null).setId(ssid);
        if (log.isWarnEnabled()) {
            log.warn("【Global exception interception】【 Throwable 】 (Global exception interception) traceId={} request= {} request failed, unknown exception {} was intercepted", ssid, uri, e);
        }
        return response;
    }

    /**
     * 获取请求的id
     *
     * @param request HttpServletRequest
     * @return 请求的ID
     */
    private String getRequestId(HttpServletRequest request) {
        String ssid = (String) request.getAttribute(webProperties.getTrackingIdentifier());
        return StringUtils.isBlank(ssid) ? TraceContext.get() : ssid;
    }

    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局异常拦截> 相关的配置");
    }

}