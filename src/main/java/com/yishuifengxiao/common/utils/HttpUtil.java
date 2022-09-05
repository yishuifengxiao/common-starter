package com.yishuifengxiao.common.utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.io.CloseUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * http工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class HttpUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 携带指定的信息重定向到指定的地址
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param url      指定的重定向地址
     * @param data     需要携带的信息
     * @throws IOException 重定向时出现异常
     */
    public synchronized static void redirect(HttpServletRequest request, HttpServletResponse response, String url, Object data) throws IOException {
        request.getSession().setAttribute("info", data);
        response.sendRedirect(url);
    }

    /**
     * 将指定的信息按照json格式输出到指定的响应
     *
     * @param response 响应
     * @param data     输出的指定信息
     */
    public synchronized static void out(HttpServletResponse response, Object data) {
        response.setStatus(HttpStatus.OK.value());
        // 允许跨域访问的域，可以是一个域的列表，也可以是通配符"*"
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 允许使用的请求方法，以逗号隔开
        response.setHeader("Access-Control-Allow-Methods", "*");
        // 是否允许请求带有验证信息，
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
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

}
