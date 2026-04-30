package com.yishuifengxiao.common.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceHelper {

    /**
     * <p>从Spring MVC的RequestMappingHandlerMapping中提取所有的URL资源信息</p>
     * <p>该方法会遍历所有的处理器方法，提取其请求路径、请求方法、类名、方法名等信息，
     * 封装成UrlResource对象列表返回。返回的结果会自动去重。</p>
     *
     * @param requestMappingHandlerMapping Spring MVC的请求映射处理器映射器，用于获取所有处理器方法信息
     * @return 包含所有URL资源的列表，每个UrlResource包含请求方法、URI、类名、方法名等信息；
     *         如果输入参数为null或没有处理器方法，则返回空列表
     */
    public static List<UrlResource> extractAllResources(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        if (requestMappingHandlerMapping == null) {
            return Collections.emptyList();
        }

        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                requestMappingHandlerMapping.getHandlerMethods();
        
        if (handlerMethods == null || handlerMethods.isEmpty()) {
            return Collections.emptyList();
        }

        List<UrlResource> list = new ArrayList<>();
        handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
            PathPatternsRequestCondition pathPatternsCondition =
                    requestMappingInfo.getPathPatternsCondition();
            
            if (pathPatternsCondition == null) {
                return;
            }

            Set<String> patternValues = pathPatternsCondition.getPatternValues();
            if (patternValues == null || patternValues.isEmpty()) {
                return;
            }

            Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
            Method method = handlerMethod.getMethod();
            
            if (method == null) {
                return;
            }

            String declaringClassName = method.getDeclaringClass().getName();
            String methodName = method.getName();

            patternValues.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(uri -> {
                        if (methods != null && !methods.isEmpty()) {
                            methods.stream()
                                    .map(requestMethod -> new UrlResource(requestMethod, uri, declaringClassName, methodName, method, null))
                                    .forEach(list::add);
                        }
                    });
        });

        return list.stream().distinct().collect(Collectors.toList());
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class UrlResource implements Serializable {
        private RequestMethod requestMethod;

        private String uri;

        private String className;

        private String methodName;

        @JsonIgnore
        private transient Method method;

        private String preAuthorize;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UrlResource that = (UrlResource) o;
            return requestMethod == that.requestMethod &&
                    Objects.equals(uri, that.uri) &&
                    Objects.equals(className, that.className) &&
                    Objects.equals(methodName, that.methodName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestMethod, uri, className, methodName);
        }
    }
}


