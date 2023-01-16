package com.yishuifengxiao.common.web;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;

import com.yishuifengxiao.common.tool.entity.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局参数校验功能自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Aspect
@EnableConfigurationProperties(ValidProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.aop", name = {"enable"}, havingValue = "true", matchIfMissing = true)
@Slf4j
public class ValidAutoConfiguration {

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody) || @annotation(com.yishuifengxiao.common.web.annotation.DataValid)")
    public void pointCut() {
    }

    /**
     * 执行环绕通知
     *
     * @param joinPoint ProceedingJoinPoint
     * @return 请求响应结果
     * @throws Throwable 处理时发生异常
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取所有的请求参数
        Object[] args = joinPoint.getArgs();
        if (null != args && args.length > 0) {
            for (Object obj : args) {
                if (obj instanceof BindingResult) {
                    BindingResult errors = (BindingResult) obj;
                    if (errors.hasErrors()) {
                        return Response.badParam(errors.getFieldErrors().get(0).getDefaultMessage());
                    }
                    break;
                }
            }
        }
        return joinPoint.proceed();

    }

    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局参数校验功能> 相关的配置");
    }

}
