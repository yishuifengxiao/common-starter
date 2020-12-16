package com.yishuifengxiao.common.aop;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;

import com.yishuifengxiao.common.tool.context.SessionStorage;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.random.UID;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局参数校验功能
 * 
 * @author yishui
 * @date 2020年6月17日
 * @version 1.0.0
 */
@Configuration
@Aspect
@EnableConfigurationProperties(AopProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.aop", name = { "enable" }, havingValue = "true", matchIfMissing = true)
@Slf4j
public class ValidAutoConfiguration {

	/**
	 * 定义切入点
	 */
	@Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody)")
	public void pointCut() {
	}

	/**
	 * 在切入点开始处切入内容
	 *
	 * @param joinPoint
	 */
	@Around("pointCut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		// 获取所有的请求参数
		Object[] args = joinPoint.getArgs();

		// 存入请求的上下文
		SessionStorage.put(RequestContext.CACHE_KEY,
				new RequestContext(UID.uuid(), joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(), args, joinPoint.getSignature().toLongString(),
						LocalDateTime.now()));

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
		try {
			return joinPoint.proceed();
		} finally {
			SessionStorage.remove(RequestContext.CACHE_KEY);
		}

	}
	
	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <全局参数校验功能> 相关的配置");
	}

}
