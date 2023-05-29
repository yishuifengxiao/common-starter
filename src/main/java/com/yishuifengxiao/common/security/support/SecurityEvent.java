package com.yishuifengxiao.common.security.support;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 处理事件
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SecurityEvent extends ApplicationEvent implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1221005789127655917L;
	private HttpServletRequest request;
    private HttpServletResponse response;
    private PropertyResource propertyResource;
    private Strategy strategy;
    private Authentication authentication;
    private Exception exception;

    @SuppressWarnings("unused")
	private SecurityEvent() {
        this(null, null, null, null, null, null, null);
    }


    public SecurityEvent(Object source, HttpServletRequest request, HttpServletResponse response,
                         PropertyResource propertyResource, Strategy strategy, Authentication authentication,
                         Exception exception) {
        super(source);
        this.request = request;
        this.response = response;
        this.propertyResource = propertyResource;
        this.strategy = strategy;
        this.authentication = authentication;
        this.exception = exception;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public PropertyResource getPropertyResource() {
        return propertyResource;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Exception getException() {
        return exception;
    }
}
