package com.yishuifengxiao.common.web;

import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


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
@ConditionalOnProperty(prefix = "yishuifengxiao.web.error", name = {"enable"}, havingValue =
        "true", matchIfMissing = false)
@Priority(1)
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
public class WebExceptionAutoConfiguration implements InitializingBean {
    /**
     * 格式化后的异常存储信息
     */
    private final static Map<String, String> errors = new HashMap<>();

    @Autowired
    private WebEnhanceProperties webProperties;

    @Autowired(required = false)
    private ErrorHelper errorHelper;

    /**
     * 捕获过滤器和拦截器中产生的异常
     *
     * @param errorAttributes ErrorAttributes
     * @param errorProperties ErrorProperties
     * @return BasicErrorController
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(@Autowired(required = false) ErrorAttributes errorAttributes, @Autowired(required = false) ErrorProperties errorProperties) {
        errorProperties = null == errorProperties ? new ErrorProperties() : errorProperties;
        return new BasicErrorController(errorAttributes, errorProperties) {
            @Override
            public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
                Object error = request.getAttribute("jakarta.servlet.error.exception");
                if (error instanceof RuntimeException ex) {
                    throw ex;
                } else if (error instanceof Exception ex) {
                    throw new RuntimeException(ex);
                }
                return super.error(request);
            }
        };
    }

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
        String ssid = TraceContext.get();
        String uri = request.getRequestURI();
        Object code = null;
        Object context = null;
        if (e instanceof UncheckedException) {
            UncheckedException ex = (UncheckedException) e;
            code = Optional.ofNullable(ex).filter(Objects::nonNull).map(UncheckedException::getCode).orElse(null);
            context = ex.getContext();
        } else if (e instanceof CustomException) {
            CustomException ex = (CustomException) e;
            code = Optional.ofNullable(ex).filter(Objects::nonNull).map(CustomException::getCode).orElse(null);
            context = ex.getContext();
        }
        code = null == code ? HttpStatus.INTERNAL_SERVER_ERROR.value() : code;
        Response<Object> response = new Response<>(code, null == e ? "" : e.getMessage(),
                context).setId(ssid);
        if (log.isDebugEnabled()) {
            log.debug("【Global exception interception】" + "traceId={} request {} " + "request " + "failed, The " + "intercepted custom " + "exception is {}", ssid, uri, e);
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
    public Object catchThrowable(HttpServletRequest request, HttpServletResponse response,
                                 Throwable e) {
        String ssid = TraceContext.get();
        String uri = request.getRequestURI();
        Object result = null == this.errorHelper ? null : this.errorHelper.extract(request,
                response, e);
        if (null == result) {
            result = createResponse(ssid, uri, e);
        }
        if (null != result && result instanceof Response<?>) {
            ((Response) result).setId(ssid);
        }

        return result;
    }


    /**
     * 根据异常生产常见问题的响应信息
     *
     * @param ssid 请求标识
     * @param uri  请求的uri
     * @param e    Throwable
     * @return 响应信息
     */
    private Response<Object> createResponse(String ssid, String uri, Throwable e) {
        //@formatter:off
        if (e instanceof MethodArgumentNotValidException ex && null !=ex.getBindingResult()) {
            BindingResult bindingResult = ex.getBindingResult();
            String msg =
                    Optional.ofNullable(bindingResult).filter(Errors::hasErrors)
                            .map(errors -> errors.getFieldErrors().stream()
                                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .filter(StringUtils::isNotBlank).findFirst().orElse(null))
                            .orElse("报文格式不符合要求");
            if (log.isDebugEnabled()) {
                log.debug("【Global exception interception】 traceId={} " + "request= {} request failed," + " The " +
                        "intercepted " + "MethodArgumentNotValidException exception is {}", ssid, uri, e);
            }
            return new Response<>(HttpStatus.BAD_REQUEST.value(), msg);
        }else {
            if (log.isInfoEnabled()) {
                log.info("【Global exception interception】 traceId={} " + "request= {} request failed," + " The " +
                        "intercepted " + "unknown exception is {}", ssid, uri, e);
            }
            String errorMsg = e == null ? null :
                    Optional.ofNullable(errors.get(e.getClass().getName())).orElse(errors.get(e.getClass().getSimpleName()));
            if (StringUtils.isNotBlank(errorMsg)) {
                return Response.error(errorMsg);
            } else  if (e instanceof HttpMessageNotReadableException) {
                return new Response<>(HttpStatus.BAD_REQUEST.value(), "报文解析失败");
            }else if (e instanceof MissingServletRequestParameterException) {
                return new Response<>(HttpStatus.BAD_REQUEST.value(), "请求报文有误");
            } else if (e instanceof MethodArgumentTypeMismatchException) {
                return new Response<>(HttpStatus.BAD_REQUEST.value(), "请求报文有误");
            } else if (e instanceof ConstraintViolationException) {
                return new Response<>(HttpStatus.BAD_REQUEST.value(), "非法报文");
            } else if (e instanceof ValidationException) {
                return new Response<>(HttpStatus.BAD_REQUEST.value(), "非法报文");
            } else if (e instanceof HttpRequestMethodNotSupportedException) {
                return new Response<>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法");
            } else if (e instanceof HttpMediaTypeNotSupportedException) {
                return new Response<>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前媒体类型");
            } else if (e instanceof NullPointerException) {
                if (log.isWarnEnabled()) {
                    log.warn("[NPE] 请求出现NPE ,错误原因为 {}", e);
                }
                return new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求处理失败");
            } else if (e instanceof IllegalArgumentException) {
                return new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "参数不符合要求");
            }
            else if (e instanceof IOException) {
                return new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求处理失败");
            } else if (e instanceof NoResourceFoundException) {
                return new Response<>(HttpStatus.NOT_FOUND.value(), "目标资源不存在");
            }

        }

        //@formatter:on
        return Response.error();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        errors.put("InsufficientAuthenticationException".trim(), "请求需要认证");
        errors.put("UsernameNotFoundException".trim(), "用户名不存在");
        errors.put("InvalidTokenException".trim(), "无效的访问令牌");
        errors.put("BadClientCredentialsException".trim(), "密码错误");
        errors.put("InvalidGrantException".trim(), "授权模方式无效");
        errors.put("InvalidClientException".trim(), "不存在对应的终端");
        errors.put("RedirectMismatchException".trim(), "请配置回调地址");
        errors.put("UnauthorizedClientException".trim(), "未获得本资源的访问授权");

        errors.put("ConstraintViolationException".trim(), "已经存在相似的数据," + "不能重复添加");
        errors.put("DataIntegrityViolationException".trim(), "已经存在相似的数据," + "不能重复添加");
        errors.put("DuplicateKeyException".trim(), "已经存在相似的数据,不能重复添加");
        errors.put("AuthenticationCredentialsNotFoundException".toLowerCase(), "您还未登录,请先登录");
        // 配置简称名字匹配的提示信息
        if (null != webProperties.getError() && null != webProperties.getError().getMap()) {
            webProperties.getError().getMap().forEach((k, v) -> {
                if (StringUtils.isNoneBlank(k, v)) {
                    errors.put(k.trim(), v.trim());
                }
            });
        }
    }

    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局异常拦截> 相关的配置");
    }
}