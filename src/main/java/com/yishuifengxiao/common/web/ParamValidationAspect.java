package com.yishuifengxiao.common.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.*;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 参数验证切面类
 * 用于处理带有特定注解的Controller方法的参数验证和返回值包装
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ParamValidationAspect {
    private final static ObjectMapper defaultObjectMapper = new ObjectMapper();
    // 校验器实例
    private final Validator validator;
    // Web增强配置属性
    private final WebEnhanceProperties webEnhanceProperties;
    private final ObjectMapper objectMapper;

    /**
     * 公共方法切入点：匹配任意映射注解
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " + //
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " + //
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +//
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")//
    public void mappingMethods() {
    }

    /**
     * 条件1：类标注 @RestController，方法标注任意映射注解
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && mappingMethods()")
    public void restControllerMethods() {
    }

    /**
     * 条件2：类标注 @Controller，方法标注映射注解且方法必须有 @ResponseBody
     */
    @Pointcut("@within(org.springframework.stereotype.Controller) && " + "mappingMethods() && " + "@annotation(org" +
            ".springframework.web.bind.annotation.ResponseBody)")
    public void controllerWithMethodResponseBody() {
    }

    /**
     * 条件3：类同时标注 @Controller 和 @ResponseBody，方法标注任意映射注解
     */
    @Pointcut("@within(org.springframework.stereotype.Controller) && " + "@within(org.springframework.web.bind" +
            ".annotation.ResponseBody) && " + "mappingMethods()")
    public void responseBodyClassMethods() {
    }

    /**
     * 组合切入点：匹配以上三种情况的并集
     */
    @Pointcut("restControllerMethods() || controllerWithMethodResponseBody() || responseBodyClassMethods()")
    public void controllerMethods() {
    }

    /**
     * 环绕通知：在控制器方法执行前后进行参数验证和返回值处理
     *
     * @param joinPoint 连接点，可以获取方法参数等信息
     * @return 方法执行结果或包装后的结果
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Around("controllerMethods()")
    public Object validateAndWrap(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取目标类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(joinPoint.getTarget());
        // 获取方法
        Method method = ClassUtils.getMostSpecificMethod(signature.getMethod(), targetClass);
        // 获取方法参数列表
        Parameter[] parameters = method.getParameters();

        // ----------------- 1. 参数校验 -----------------
        // 如果没有参数，直接执行方法
        if (args.length == 0) {
            return proceedAndWrap(joinPoint, method, targetClass);
        }

        BindingResult bindingResult = null;
        List<ValidatedParam> validatedParams = null;

        // 遍历参数进行处理
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            // 检查是否是BindingResult类型
            if (BindingResult.class.isAssignableFrom(param.getType())) {
                bindingResult = (BindingResult) args[i];
                continue;
            }
            // 检查是否有@Valid或@Validated注解
            if (param.isAnnotationPresent(Valid.class) || param.isAnnotationPresent(Validated.class)) {
                if (validatedParams == null) {
                    validatedParams = new ArrayList<>();
                }
                ValidatedParam vp = new ValidatedParam();
                vp.value = args[i];
                vp.groups = getValidationGroups(param);
                validatedParams.add(vp);
            }
        }

        // 处理BindingResult中的错误
        if (bindingResult != null && bindingResult.hasErrors()) {
            BindException bindException = new BindException(bindingResult);
            String msg =
                    Optional.ofNullable(bindingResult.getFieldError()).map(FieldError::getDefaultMessage).orElse(
                            "参数校验失败");
            throw new UncheckedException(msg, bindException);
        }

        // 处理带有@Valid或@Validated注解的参数验证
        if (validatedParams != null) {
            for (ValidatedParam vp : validatedParams) {
                if (vp.value == null) {
                    continue;
                }
                Set<ConstraintViolation<Object>> violations;
                if (vp.groups != null && vp.groups.length > 0) {
                    violations = getValidator().validate(vp.value, vp.groups);
                } else {
                    violations = getValidator().validate(vp.value, Default.class);
                }
                if (!violations.isEmpty()) {
                    ConstraintViolationException ex = new ConstraintViolationException(violations);
                    String msg = violations.iterator().next().getMessage();
                    throw new UncheckedException(msg, ex);
                }
            }
        }

        // ----------------- 2. 执行控制器方法 -----------------
        return proceedAndWrap(joinPoint, method, targetClass);
    }

    /**
     * 执行目标方法并处理返回值
     *
     * @param joinPoint   连接点
     * @param method      目标方法
     * @param targetClass 目标类
     * @return 方法执行结果或包装后的结果
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    private Object proceedAndWrap(ProceedingJoinPoint joinPoint, Method method, Class<?> targetClass) throws Throwable {
        Object result = joinPoint.proceed();

        // ----------------- 3. void 返回值包装 -----------------
        if (method.getReturnType() == void.class && result == null) {
            if (!shouldWrapResponse(targetClass, method)) {
                return null;
            }
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletResponse response = attributes.getResponse();
                if (response == null || response.isCommitted()) {
                    // 响应已提交，避免重复写入
                    return null;
                } else {
                    try {
                        // 构造统一响应（data 设为 null）
                        Response<Object> resp = Response.suc().setData(null);
                        String requestId = TraceContext.get();
                        resp.setRequestId(requestId);

                        // 设置响应头
                        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        response.setStatus(HttpServletResponse.SC_OK);

                        // 写出 JSON
                        PrintWriter writer = response.getWriter();
                        getObjectMapper().writeValue(writer, resp);
                        writer.flush();
                    } catch (Exception e) {
                        log.error("Failed to write void response for class {} method: {}", targetClass.getName(),
                                method.getName(), e);
                    }
                }
            }
            String ssid = TraceContext.get();
            return Response.suc().setRequestId(ssid);
        }

        return result;
    }

    /**
     * 判断是否需要进行响应包装
     *
     * @param targetClass 目标类
     * @param method      目标方法
     * @return 是否需要包装
     */
    private boolean shouldWrapResponse(Class<?> targetClass, Method method) {
        if (webEnhanceProperties == null || webEnhanceProperties.getResponse() == null
                || !Boolean.TRUE.equals(webEnhanceProperties.getResponse().getEnable())) {
            return false;
        }
        if (AnnotatedElementUtils.findMergedAnnotation(targetClass, SkipResponseWrapper.class) != null ||
                AnnotatedElementUtils.findMergedAnnotation(method, SkipResponseWrapper.class) != null) {
            return false;
        }
        List<String> excludes = webEnhanceProperties.getResponse().getExcludes();
        return !(excludes != null && excludes.contains(targetClass.getName()));
    }

    /**
     * 获取验证分组
     *
     * @param parameter 方法参数
     * @return 验证分组数组
     */
    private Class<?>[] getValidationGroups(Parameter parameter) {
        Validated validated = parameter.getAnnotation(Validated.class);
        if (validated != null && validated.value().length > 0) {
            return validated.value();
        }
        return null;
    }

    /**
     * 获取校验器实例
     * <p>优先使用注入的 Validator，若为 null 则使用默认 ValidatorFactory 创建</p>
     *
     * @return 校验器实例
     */
    private Validator getValidator() {
        return validator != null ? validator : Validation.buildDefaultValidatorFactory().getValidator();
    }


    /**
     * 获取ObjectMapper实例的方法
     * 如果objectMapper不为null，则返回objectMapper；否则返回defaultObjectMapper
     *
     * @return 返回ObjectMapper实例，可能是自定义的objectMapper或默认的defaultObjectMapper
     */
    private ObjectMapper getObjectMapper() {
        return objectMapper != null ? objectMapper : defaultObjectMapper;
    }

    /**
     * 内部类：用于存储需要验证的参数信息
     */
    private static class ValidatedParam {
        Object value;      // 参数值
        Class<?>[] groups; // 验证分组
    }
}