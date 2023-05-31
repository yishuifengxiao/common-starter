package com.yishuifengxiao.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.io.CloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * http工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class HttpUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * json请求标志
     */
    private final static String JSON_FLAG = "json";

    /**
     * 携带指定的信息重定向到指定的地址
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param url      指定的重定向地址
     * @param data     需要携带的信息
     * @throws IOException 重定向时出现异常
     */
    public synchronized static void redirect(HttpServletRequest request, HttpServletResponse response, String url,
                                             Object data) throws IOException {
        request.getSession().setAttribute("info", data);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.sendRedirect(url);
    }

    /**
     * 将指定的信息按照json格式输出到指定的响应
     *
     * @param response 响应
     * @param data     输出的指定信息
     */
    public synchronized static void write(HttpServletResponse response, Object data) {
        write(null, response, data);
    }

    /**
     * 将指定的信息按照json格式输出到指定的响应
     *
     * @param request  HttpServletRequest
     * @param response 响应
     * @param data     输出的指定信息
     */
    public synchronized static void write(HttpServletRequest request, HttpServletResponse response, Object data) {
        response.setStatus(HttpStatus.OK.value());
        if (StringUtils.isBlank(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))) {
            // 允许跨域访问的域，可以是一个域的列表，也可以是通配符"*"
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, accessControlAllowOrigin(request));
        }
        if (StringUtils.isBlank(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))) {
            // 允许使用的请求方法，以逗号隔开
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        }
        if (StringUtils.isBlank(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS))) {
            // 是否允许请求带有验证信息，
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        if (StringUtils.isBlank(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))) {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, accessControlAllowHeaders(request, response));
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            response.getWriter().write(MAPPER.writeValueAsString(data));
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            log.info("返回响应数据时出现问题，出现问题的原因为 {}", e.getMessage());
        }
    }

    /**
     * 打印请求中携带的查询参数和请求头信息
     *
     * @param request HttpServletRequest
     */
    public static void stack(HttpServletRequest request) {
        log.debug("\r\n");
        log.debug("==start  用户请求的请求参数中包含的信息为 query start ===");
        Map<String, String[]> params = request.getParameterMap();
        if (null != params) {
            params.forEach((k, v) -> {
                log.debug("请求参数中的参数名字为 {},对应的值为 {}", k, StringUtils.join(v, " , "));
            });
        }
        log.debug("==end  用户请求的请求参数中包含的信息为  query end ===");
        log.debug("==start  用户请求的请求头中包含的信息为 header start ===");
        for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
            String name = e.nextElement();
            log.debug("请求头的名字为 {},对应的值为 {}", name, request.getHeader(name));
        }
        log.debug("==end  用户请求的请求头中包含的信息为 header end ===");
        log.debug("\r\n");
    }

    /**
     * 根据输入流下载文件
     *
     * @param response    HttpServletResponse
     * @param inputStream 输入流
     * @param fileName    待下载的文件的名字
     */
    public static void down(HttpServletResponse response, InputStream inputStream, String fileName) {
        fileName = null == fileName ? System.currentTimeMillis() + "" : fileName.trim();
        OutputStream outputStream = null;
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            FileCopyUtils.copy(inputStream, outputStream);
            outputStream.flush();
            CloseUtil.close(inputStream, outputStream);
        } catch (Exception e) {
            log.info("下载文件{}时出现问题，出现问题的原因为 {}", fileName, e);
            throw new UncheckedException("文件下载失败");
        } finally {
            CloseUtil.close(outputStream, inputStream);
        }
    }

    /**
     * 是否为json请求
     *
     * @param request HttpServletRequest
     * @return true标识为json请求，false不是json请求
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        String method = request.getMethod();
        if (!StringUtils.equalsIgnoreCase(HttpMethod.GET.toString(), method)) {
            // 不是get请求
            return true;
        }
        String contentType = request.getContentType();
        if (StringUtils.containsIgnoreCase(contentType, JSON_FLAG)) {
            return true;
        }
        String accept = null;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement();
            if (StringUtils.equalsIgnoreCase(nextElement, HttpHeaders.ACCEPT)) {
                accept = nextElement;
                break;
            }
        }
        if (StringUtils.isBlank(accept)) {
            return false;
        }
        final String acceptVal = request.getHeader(accept);
        if (StringUtils.containsIgnoreCase(acceptVal, JSON_FLAG)) {
            return true;
        }
        return false;
    }

    /**
     * 获取请求中的浏览器标识
     *
     * @param request HttpServletRequest
     * @return User-Agent
     */
    public static String userAgent(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.USER_AGENT);
        if (StringUtils.isNotBlank(header)) {
            return header;
        }
        String headerName = null;
        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String element = headerNames.nextElement();
            if (StringUtils.equalsIgnoreCase(element, HttpHeaders.USER_AGENT) || StringUtils.equalsIgnoreCase(element
                    , "UserAgent")) {
                headerName = element;
                break;
            }
        }
        return StringUtils.isBlank(headerName) ? null : request.getHeader(headerName);
    }

    /**
     * <p>从HttpServletRequest提取出合适的Access-Control-Allow-Origin值</p>
     * <p>优先从请求头的<code>Origin</code>中获取，然后从优先从请求头的<code>Referer</code>中获取获取协议和域名；最后默认设置为*</p>
     *
     * @param request HttpServletRequest
     * @return
     */
    public static String accessControlAllowOrigin(HttpServletRequest request) {
        String value = request.getHeader(HttpHeaders.ORIGIN);
        if (StringUtils.isBlank(value)) {
            value = request.getHeader(HttpHeaders.REFERER);
            if (StringUtils.isNotBlank(value)) {
                int indexOf = StringUtils.indexOf(value, "/", StringUtils.indexOf(value, "//") + 2);
                value = indexOf != -1 ? value.substring(0, indexOf) : StringUtils.substringBefore(value, "?");
            }
        }
        return StringUtils.isBlank(value) ? "*" : value;
    }


    /**
     * 默认的Access-Control-Allow-Headers的值
     *
     * @see
     * <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Headers">https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Headers</a>
     */
    private final static Set<String> DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS = DataUtil.asSet(
            // @formatter:off
            TokenConstant.TOKEN_REQUEST_PARAM,
            TokenConstant.TOKEN_HEADER_PARAM,
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.WWW_AUTHENTICATE,
            HttpHeaders.HOST,
            HttpHeaders.ORIGIN,
            HttpHeaders.REFERER,
            HttpHeaders.VARY,
            HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS
            // @formatter:on
    );

    /**
     * <p>从HttpServletRequest和HttpServletResponse提取出所有的请求头</p>
     * <p>不包含原始的Access-Control-Allow-Headers</p>
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return HttpServletRequest和HttpServletResponse提取出所有的请求头
     */
    public static String accessControlAllowHeaders(HttpServletRequest request, HttpServletResponse response) {
        Set<String> sets = new HashSet<>();
        //注意以下这些特定的首部是一直允许的：Accept, Accept-Language, Content-Language, Content-Type（但只在其值属于 MIME 类型
        // application/x-www-form-urlencoded, multipart/form-data 或 text/plain中的一种时）。这些被称作simple headers，你无需特意声明它们
//        在使用CORS方式跨域时，浏览器只会返回 默认的头部 Header，认情况下可用的响应头包括：
//        Cache-Control 、 Content-Language、 Content-Type 、 Expires、  Last-Modified、 Pragma CORS规范将这些头称为 简单响应头
//        Access-Control-Expose-Headers 是一个 CORS（跨域资源共享）头部，用于指定允许客户端访问的响应头信息。默认情况下，浏览器会阻止跨域请求获取响应头信息，因此需要在服务器端设置
//        Access-Control-Expose-Headers 来允许客户端访问指定的响应头。
        String accessControlAllowHeaders = response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        if (StringUtils.isNotBlank(accessControlAllowHeaders)) {
            sets.addAll(Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(accessControlAllowHeaders,
                    ",")));
        }
        String accessControlRequestHeaders = request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        if (StringUtils.isNotBlank(accessControlRequestHeaders)) {
            sets.addAll(Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(accessControlRequestHeaders,
                    ",")));
        }
        sets.addAll(DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS);
        accessControlAllowHeaders =
                sets.stream().filter(StringUtils::isNotBlank).map(String::toLowerCase).distinct().collect(Collectors.joining(","));
        return accessControlAllowHeaders;
    }


}
