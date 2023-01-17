package com.yishuifengxiao.common.web.error;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.web.WebExceptionProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 简单实现的异常信息补充提取工具
 *
 * @author qingteng
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleExceptionHelper implements ExceptionHelper, InitializingBean {

    /**
     * 格式化后的异常存储信息
     */
    private final static Map<String, String> errors = new HashMap<>();

    private ErrorHandler errorHandler;

    private WebExceptionProperties exceptionProperties;

    @Override
    public Response<Object> extract(Throwable e, String defaultMsg) {
        Response<Object> response = null;
        if (null != this.errorHandler) {
            response = this.errorHandler.extractErrorMsg(exceptionProperties, e);
        }
        if (null != response) {
            return response;
        }
        response = createResponse(e);
        return null != response ? response : Response.error(getErrorMsg(e, defaultMsg));
    }

    /**
     * 根据异常生产常见问题的响应信息
     *
     * @return 响应信息
     */
    private Response<Object> createResponse(Throwable e) {
        if (e instanceof HttpMessageNotReadableException) {
            return new Response<>(HttpStatus.BAD_REQUEST.value(), "参数解析失败");
        } else if (e instanceof IllegalArgumentException) {
            return new Response<>(HttpStatus.BAD_REQUEST.value(), "参数不符合要求");
        } else if (e instanceof MissingServletRequestParameterException) {
            return new Response<>(HttpStatus.BAD_REQUEST.value(), "请求参数有误");
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            return new Response<>(HttpStatus.BAD_REQUEST.value(), "请求参数有误");
        } else if (e instanceof ValidationException) {
            return new Response<>(HttpStatus.BAD_REQUEST.value(), "非法参数");
        } else if (e instanceof ConstraintViolationException) {
            return new Response<>(HttpStatus.BAD_REQUEST.value(), "非法参数");
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return new Response<>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法");
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            return new Response<>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前媒体类型");
        } else if (e instanceof NullPointerException) {
            if (log.isWarnEnabled()) {
                log.warn("[NPE] 请求出现NPE");
            }
            return new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求处理失败");
        } else if (e instanceof IOException) {
            return new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求处理失败");
        }
        return null;
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
     * @param e          异常信息
     * @param defaultMsg 自定义提示信息
     * @return 异常提示信息
     */
    public String getErrorMsg(Throwable e, String defaultMsg) {
        if (null == e) {
            return "未知异常";
        }
        // 全称信息提示
        String msg = SimpleExceptionHelper.errors.get(e.getClass().getName().toLowerCase());

        // 根据简称查找
        if (StringUtils.isBlank(msg)) {
            msg = SimpleExceptionHelper.errors.get(e.getClass().getSimpleName().toLowerCase());
        }

        // 返回默认信息
        if (StringUtils.isBlank(msg)) {
            return StringUtils.isNotBlank(defaultMsg) ? defaultMsg : e.getMessage();
        }
        return msg;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SimpleExceptionHelper.errors.put("InsufficientAuthenticationException".toLowerCase(), "请求需要认证");
        SimpleExceptionHelper.errors.put("UsernameNotFoundException".toLowerCase(), "用户名不存在");
        SimpleExceptionHelper.errors.put("InvalidTokenException".toLowerCase(), "无效的访问令牌");
        SimpleExceptionHelper.errors.put("BadClientCredentialsException".toLowerCase(), "密码错误");
        SimpleExceptionHelper.errors.put("InvalidGrantException".toLowerCase(), "授权模方式无效");
        SimpleExceptionHelper.errors.put("InvalidClientException".toLowerCase(), "不存在对应的终端");
        SimpleExceptionHelper.errors.put("RedirectMismatchException".toLowerCase(), "请配置回调地址");
        SimpleExceptionHelper.errors.put("UnauthorizedClientException".toLowerCase(), "未获得本资源的访问授权");

        SimpleExceptionHelper.errors.put("ConstraintViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
        SimpleExceptionHelper.errors.put("DataIntegrityViolationException".toLowerCase(), "已经存在相似的数据,不能重复添加");
        SimpleExceptionHelper.errors.put("DuplicateKeyException".toLowerCase(), "已经存在相似的数据,不能重复添加");
        SimpleExceptionHelper.errors.put("AuthenticationCredentialsNotFoundException".toLowerCase(), "您还未登录,请先登录");
        // 配置简称名字匹配的提示信息
        if (null != exceptionProperties.getMap()) {
            exceptionProperties.getMap().forEach((k, v) -> {
                if (StringUtils.isNoneBlank(k, v)) {
                    SimpleExceptionHelper.errors.put(k.toLowerCase(), v);
                }
            });
        }
        // 配置全称名字匹配的提示信息
        if (null != exceptionProperties.getFull()) {
            exceptionProperties.getFull().forEach((k, v) -> {
                if (StringUtils.isNoneBlank(k, v)) {
                    SimpleExceptionHelper.errors.put(k.toLowerCase(), v);
                }
            });
        }

    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setExceptionProperties(WebExceptionProperties exceptionProperties) {
        this.exceptionProperties = exceptionProperties;
    }

}
