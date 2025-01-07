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

    public static List<UrlResource> extractAllResources(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        //@formatter:off
        List<UrlResource> list = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
            PathPatternsRequestCondition pathPatternsCondition =
                    requestMappingInfo.getPathPatternsCondition();
            Set<String> patternValues = pathPatternsCondition.getPatternValues();
            Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
            Method method = handlerMethod.getMethod();

            List<UrlResource> permissions = patternValues.parallelStream().filter(StringUtils::isNotBlank)
            .map(uri -> methods.stream().map(s -> new UrlResource(s, uri, method.getDeclaringClass().getName(),
            method.getName(), method,null)).collect(Collectors.toList())).filter(Objects::nonNull)
            .flatMap(Collection::stream).collect(Collectors.toList());
            list.addAll(permissions);
        });
        //@formatter:on
        return list.stream().distinct().collect(Collectors.toList());
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UrlResource implements Serializable {
        private RequestMethod requestMethod;

        private String uri;

        private String className;

        private String methodName;

        @JsonIgnore
        private transient Method method;

        private String preAuthorize;
    }
}


