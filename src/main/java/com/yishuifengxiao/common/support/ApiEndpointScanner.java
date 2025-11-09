package com.yishuifengxiao.common.support;

import com.yishuifengxiao.common.support.api.ApiInfo;
import com.yishuifengxiao.common.support.api.ApiInfoExpander;
import com.yishuifengxiao.common.support.api.ApiMethod;
import com.yishuifengxiao.common.support.api.ApiModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * API端点扫描工具类
 * 用于扫描Spring Boot项目中的所有接口路径和自定义注解信息
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ApiEndpointScanner {

    private final ApplicationContext applicationContext;
    private volatile List<ApiInfo> cachedApiInfoList = null;

    /**
     * 构造一个API端点扫描器实例
     *
     * @param applicationContext Spring应用上下文，用于获取Bean和扫描API端点
     */
    public ApiEndpointScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<ApiInfo> allApiEndpoints() {
        List<ApiInfo> apiInfos = this.scanAllApiEndpoints();
        return ApiInfoExpander.expandAndSortApiInfoList(apiInfos);
    }

    /**
     * 获取所有API端点信息
     *
     * @return API信息列表
     */
    public List<ApiInfo> scanAllApiEndpoints() {
        if (cachedApiInfoList != null) {
            return cachedApiInfoList;
        }

        final List<ApiInfo> apiInfoList = new ArrayList<>();

        try {
            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
            handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
                Class<?> controllerClass = handlerMethod.getBeanType();
                Method method = handlerMethod.getMethod();
                ApiInfo apiInfo = new ApiInfo();
                apiInfo.setClassName(controllerClass.getName());
                apiInfo.setMethodName(method.getName());
                Set<String> paths = requestMappingInfo.getPatternValues().stream().collect(Collectors.toSet());
                if (paths.isEmpty()) {
                    paths = requestMappingInfo.getDirectPaths().stream().collect(Collectors.toSet());
                }
                apiInfo.setPath(paths);
                Set<String> httpMethods = requestMappingInfo.getMethodsCondition().getMethods().stream().map(Enum::name).collect(Collectors.toSet());
                if (httpMethods.isEmpty()) {
                    httpMethods = Set.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD");
                }
                apiInfo.setHttpMethods(httpMethods);
                ApiModule apiModule = AnnotationUtils.findAnnotation(controllerClass, ApiModule.class);
                if (apiModule != null) {
                    apiInfo.setModuleName(apiModule.value());
                    apiInfo.setModuleDescription(apiModule.description());
                }
                ApiMethod apiMethod = AnnotationUtils.findAnnotation(method, ApiMethod.class);
                if (null != apiMethod) {
                    apiInfo.setMethodPermission(apiMethod.permission());
                    apiInfo.setMethodValue(apiMethod.value());
                    apiInfo.setMethodDescription(apiMethod.description());
                }
                apiInfoList.add(apiInfo);
            });

        } catch (Exception e) {
            log.error("扫描API端点时发生错误", e);
        }
        List<ApiInfo> result = apiInfoList.stream().sorted(Comparator.comparing(ApiInfo::getClassName)).collect(Collectors.toList());
        cachedApiInfoList = result;
        return result;
    }

}
