package com.yishuifengxiao.common.web;


import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * 响应包装通知
 * <p>基于 ResponseBodyAdvice 实现，在 Spring MVC 消息转换器序列化前对控制器方法的返回值进行统一包装。
 * 相比 AOP 方式，此方式不会出现 ClassCastException，因为包装发生在序列化阶段而非方法代理阶段。</p>
 * <p>仅处理非 void 返回值的包装；void 返回值包装由 ParamValidationAspect 负责。</p>
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ResponseWrapAdvice implements ResponseBodyAdvice<Object> {

    private final WebEnhanceProperties webEnhanceProperties;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (webEnhanceProperties == null || webEnhanceProperties.getResponse() == null) {
            return false;
        }
        boolean enable = Boolean.TRUE.equals(webEnhanceProperties.getResponse().getEnable());
        if (!enable) {
            return false;
        }

        if (AnnotatedElementUtils.findMergedAnnotation(returnType.getMethod(), SkipResponseWrapper.class) != null) {
            return false;
        }

        Class<?> declaringClass = returnType.getDeclaringClass();
        if (declaringClass == null) {
            return false;
        }

        if (AnnotatedElementUtils.findMergedAnnotation(declaringClass, SkipResponseWrapper.class) != null) {
            return false;
        }

        String className = declaringClass.getName();
        List<String> excludes = webEnhanceProperties.getResponse().getExcludes();
        if (excludes != null) {
            boolean excluded = excludes.stream()
                    .anyMatch(v -> StringUtils.equalsIgnoreCase(v, className));
            if (excluded) {
                return false;
            }
        }

        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof String || body instanceof Response) {
            return body;
        }

        if (body == null) {
            if (returnType.getParameterType() == void.class) {
                String ssid = TraceContext.get();
                return Response.suc().setRequestId(ssid);
            }
            return null;
        }

        if (!AbstractJackson2HttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            return body;
        }

        try {
            String ssid = TraceContext.get();
            Response<Object> wrapped = Response.suc().setData(body);
            wrapped.setRequestId(ssid);
            return wrapped;
        } catch (Exception e) {
            log.debug("【yishuifengxiao-common-spring-boot-starter】: 响应包装失败 {}", e.getMessage());
        }

        return body;
    }
}
