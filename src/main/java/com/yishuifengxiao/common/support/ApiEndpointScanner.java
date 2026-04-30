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

    private static final Set<String> DEFAULT_HTTP_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
    )));

    private final ApplicationContext applicationContext;
    private volatile List<ApiInfo> cachedApiInfoList = null;
    private final Object cacheLock = new Object();

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

        synchronized (cacheLock) {
            if (cachedApiInfoList != null) {
                return cachedApiInfoList;
            }

            List<ApiInfo> apiInfoList = doScanApiEndpoints();
            List<ApiInfo> result = apiInfoList.stream()
                    .sorted(Comparator.comparing(ApiInfo::getClassName))
                    .collect(Collectors.toList());
            cachedApiInfoList = result;
            return result;
        }
    }

    /**
     * 执行实际的API端点扫描逻辑
     *
     * @return 扫描到的API信息列表
     */
    private List<ApiInfo> doScanApiEndpoints() {
        List<ApiInfo> apiInfoList = new ArrayList<>();

        try {
            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
            
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo requestMappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();
                
                ApiInfo apiInfo = buildApiInfo(requestMappingInfo, handlerMethod);
                if (apiInfo != null) {
                    apiInfoList.add(apiInfo);
                }
            }

        } catch (Exception e) {
            log.error("扫描API端点时发生错误", e);
        }
        
        return apiInfoList;
    }

    /**
     * 构建单个API信息对象
     *
     * @param requestMappingInfo 请求映射信息
     * @param handlerMethod      处理器方法
     * @return API信息对象，如果构建失败则返回null
     */
    private ApiInfo buildApiInfo(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        try {
            Class<?> controllerClass = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();
            
            if (method == null) {
                return null;
            }

            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setClassName(controllerClass.getName());
            apiInfo.setMethodName(method.getName());

            // 设置路径
            Set<String> paths = extractPaths(requestMappingInfo);
            apiInfo.setPath(paths);

            // 设置HTTP方法
            Set<String> httpMethods = extractHttpMethods(requestMappingInfo);
            apiInfo.setHttpMethods(httpMethods);

            // 设置模块注解信息
            ApiModule apiModule = AnnotationUtils.findAnnotation(controllerClass, ApiModule.class);
            if (apiModule != null) {
                apiInfo.setModuleName(apiModule.value());
                apiInfo.setModuleDescription(apiModule.description());
            }

            // 设置方法注解信息
            ApiMethod apiMethod = AnnotationUtils.findAnnotation(method, ApiMethod.class);
            if (apiMethod != null) {
                apiInfo.setMethodPermission(apiMethod.permission());
                apiInfo.setMethodValue(apiMethod.value());
                apiInfo.setMethodDescription(apiMethod.description());
            }

            return apiInfo;
        } catch (Exception e) {
            log.warn("构建API信息时发生错误，跳过该端点", e);
            return null;
        }
    }

    /**
     * 提取请求路径
     *
     * @param requestMappingInfo 请求映射信息
     * @return 路径集合
     */
    private Set<String> extractPaths(RequestMappingInfo requestMappingInfo) {
        Set<String> paths = requestMappingInfo.getPatternValues();
        if (paths == null || paths.isEmpty()) {
            paths = requestMappingInfo.getDirectPaths();
        }
        if (paths == null) {
            paths = Collections.emptySet();
        }
        return paths.stream().collect(Collectors.toSet());
    }

    /**
     * 提取HTTP方法
     *
     * @param requestMappingInfo 请求映射信息
     * @return HTTP方法集合
     */
    private Set<String> extractHttpMethods(RequestMappingInfo requestMappingInfo) {
        Set<String> httpMethods = requestMappingInfo.getMethodsCondition().getMethods()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        
        if (httpMethods.isEmpty()) {
            return DEFAULT_HTTP_METHODS;
        }
        return httpMethods;
    }

}
