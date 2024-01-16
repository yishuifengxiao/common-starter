package com.yishuifengxiao.common.web;

import com.yishuifengxiao.common.support.I18nHelper;
import com.yishuifengxiao.common.support.TraceContext;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.web.error.ErrorHelper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


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
@ConditionalOnProperty(prefix = "yishuifengxiao.web.error", name = {"enable"}, havingValue = "true", matchIfMissing = true)
@Priority(1)
public class WebExceptionAutoConfiguration implements InitializingBean {
    /**
     * 格式化后的异常存储信息
     */
    private final static Map<String, String> errors = new HashMap<>();
    @Autowired
    private WebEnhanceProperties webProperties;

    @Autowired(required = false)
    private ErrorHelper errorHelper;

    private ErrorHelper proxyErrorHelper;
    @Autowired
    private I18nHelper i18nHelper;


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
        String ssid = this.getRequestId(request);
        String uri = null != request ? request.getRequestURI() : null;
        Integer code = null;
        if (e instanceof UncheckedException ex) {
            code = ex.getCode();
        } else if (e instanceof CustomException ex) {
            code = ex.getCode();
        }
        code = null == code ? HttpStatus.INTERNAL_SERVER_ERROR.value() : code;
        Response<String> response = new Response<String>(code, i18nHelper.getMessage(request, e.getMessage())).setId(ssid);
        if (log.isDebugEnabled()) {
            log.debug("【Global exception interception】【 CustomException 】 traceId={} request {} " + "request failed, " + "The intercepted custom exception is {}", ssid, uri, e);
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
    public Object catchThrowable(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        String ssid = this.getRequestId(request);
        String uri = null != request ? request.getRequestURI() : null;
        Object result = this.proxyErrorHelper.extract(request, response, e);
        if (log.isInfoEnabled()) {
            log.info("【Global exception interception】【 Throwable 】 traceId={} " + "request= {} request failed, The intercepted unknown exception is {}", ssid, uri, e);
        }
        return result;
    }

    /**
     * 获取请求的id
     *
     * @param request HttpServletRequest
     * @return 请求的ID
     */
    private String getRequestId(HttpServletRequest request) {
        String ssid = (String) request.getAttribute(webProperties.getTracked());
        return StringUtils.isBlank(ssid) ? TraceContext.get() : ssid;
    }


    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局异常拦截> 相关的配置");
    }


    /**
     * <p>
     * 根据异常从配置信息中获取异常信息
     * </p>
     * <p>
     * 提取过程如下
     * <ul>
     * <li>先根据异常类的完整名字获取异常提示信息</li>
     * <li>如果第一步中没有获取异常信息，则根据异常类的名字(不区分大小)获取异常提示信息</li>
     * <li>如果还是没有获取到异常提示信息，且用户配置的提示信息不为空，则使用用户配置的第一个提示信息作为异常提示信息</li>
     * <li>如果还是没有获取到异常提示信息，就是用原来的异常类里的信息</li>
     * </ul>
     *
     * @param e 异常信息
     * @return 异常提示信息
     */
    private String getErrorMsg(Throwable e) {
        if (null == e) {
            return "未知异常";
        }
        // 全称信息提示
        String msg = errors.get(e.getClass().getName().toLowerCase());
        if (null != msg) {
            return msg;
        }
        // 根据简称查找
        return errors.get(e.getClass().getSimpleName().toLowerCase());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        errors.put("InsufficientAuthenticationException".toLowerCase(), "请求需要认证");
        errors.put("UsernameNotFoundException".toLowerCase(), "用户名不存在");
        errors.put("InvalidTokenException".toLowerCase(), "无效的访问令牌");
        errors.put("BadClientCredentialsException".toLowerCase(), "密码错误");
        errors.put("InvalidGrantException".toLowerCase(), "授权模方式无效");
        errors.put("InvalidClientException".toLowerCase(), "不存在对应的终端");
        errors.put("RedirectMismatchException".toLowerCase(), "请配置回调地址");
        errors.put("UnauthorizedClientException".toLowerCase(), "未获得本资源的访问授权");

        errors.put("ConstraintViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
        errors.put("DataIntegrityViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
        errors.put("DuplicateKeyException".toLowerCase(), "已经存在相似的数据,不能重复添加");
        errors.put("AuthenticationCredentialsNotFoundException".toLowerCase(), "您还未登录,请先登录");
        if (null != webProperties.getError() && null != webProperties.getError().getMap()) {
            // 配置简称名字匹配的提示信息
            webProperties.getError().getMap().forEach((k, v) -> {
                if (StringUtils.isNoneBlank(k, v)) {
                    errors.put(k.toLowerCase(), v);
                }
            });
        }

        this.proxyErrorHelper = (request, response, e) -> {
            if (null != errorHelper) {
                Object result = errorHelper.extract(request, response, e);
                if (null != result) {
                    return result;
                }
            }
            return createResponse(request, response, e);
        };
    }

    /**
     * 根据异常生产常见问题的响应信息
     *
     * @return 响应信息
     */
    private Response<Object> createResponse(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Response result = null;
        String errorMsg = getErrorMsg(e);
        if (StringUtils.isNotBlank(errorMsg)) {
            result = Response.error(errorMsg);
        } else {
            if (e instanceof HttpMessageNotReadableException) {
                result = new Response<>(HttpStatus.BAD_REQUEST.value(), "参数解析失败");
            } else if (e instanceof IllegalArgumentException) {
                result = new Response<>(HttpStatus.BAD_REQUEST.value(), "参数不符合要求");
            } else if (e instanceof MissingServletRequestParameterException) {
                result = new Response<>(HttpStatus.BAD_REQUEST.value(), "请求参数有误");
            } else if (e instanceof MethodArgumentTypeMismatchException) {
                result = new Response<>(HttpStatus.BAD_REQUEST.value(), "请求参数有误");
            } else if (e instanceof ValidationException) {
                result = new Response<>(HttpStatus.BAD_REQUEST.value(), "非法参数");
            } else if (e instanceof HttpRequestMethodNotSupportedException) {
                result = new Response<>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法");
            } else if (e instanceof HttpMediaTypeNotSupportedException) {
                result = new Response<>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前媒体类型");
            } else if (e instanceof NullPointerException) {
                if (log.isWarnEnabled()) {
                    log.warn("[NPE] 请求出现NPE ,错误原因为 {}", e);
                }
                result = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求处理失败");
            } else if (e instanceof IOException) {
                result = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求处理失败");
            } else {
                result = Response.error("未知异常");
            }
        }
        return result.setMsg(i18nHelper.getMessage(request, result.getMsg()));
    }

}