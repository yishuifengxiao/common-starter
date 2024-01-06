package com.yishuifengxiao.common.web;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * web增强支持支持属性配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.web")
public class WebEnhanceProperties {

    /**
     * 是否开启增强支持,默认开启
     */
    private Boolean enable = true;

    /**
     * 是否开启AOP切面功能
     */
    private Boolean aop = true;


    /**
     * 出现异常时默认的视图的名字: error
     */
    private String defaultView = "error";

    /**
     * 请求追踪标识符的名字
     */
    private String tracked = "request-traced-ssid";

    /**
     * 是否开启动态修改日志级别功能，若不为空则表示开启此功能
     */
    private String dynamicLogLevel = "dynamicLogLevel";

    /**
     * 响应增强功能配置
     */
    private ResponseProperties response = new ResponseProperties();


    /**
     * 跨域支持功能配置
     */
    private CorsProperties cors = new CorsProperties();

    /**
     * 全局异常增强功能配置
     */
    private WebExceptionProperties error = new WebExceptionProperties();

    /**
     * 跨域支持属性配置
     *
     * @author yishui
     * @version 1.0.0
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorsProperties {
        /**
         * 是否开启跨域支持,默认开启
         */
        private Boolean enable = true;


        /**
         * 跨域设置允许的路径，默认为所有路径(/*)
         */
        private String url = "/*";
        /**
         * 跨域设置允许的Origins，默认为所有
         */
        private String allowedOrigins;
        /**
         * 跨域设置允许的请求方法，*表示为所有，也可以为 GET,POST,OPTIONS,PUT,DELETE这种形式
         */
        private String allowedMethods =
                Arrays.stream(HttpMethod.values()).map(Objects::toString).collect(Collectors.joining(","));
        /**
         * 跨域设置允许的请求头，默认为所有
         */
        private String allowedHeaders;
        /**
         * 跨域设置是否允许携带凭据，默认为true
         */
        private Boolean allowCredentials = true;

        /**
         * 需要添加的响应头
         */
        private Map<String, String> headers = new HashMap<>();

        /**
         * 获取要设置要注册筛选器的URL模式
         *
         * @return 要设置要注册筛选器的URL模式
         */
        public List<String> getUrlPatterns() {
            return DataUtil.asList(StringUtils.splitByWholeSeparator(this.url, ",")).stream()
                    .filter(StringUtils::isNotBlank).map(StringUtils::trim).collect(Collectors.toList());
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebExceptionProperties {
        /**
         * 是否开启全局异常拦截功能，默认为开启
         */
        private Boolean enable = true;
        /**
         * 简单异常提示信息存储 <br/>
         * key：异常类型的名字，如 ConstraintViolationException <br/>
         * value：提示信息
         */
        private Map<String, String> map = new HashMap<>();

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseProperties {

        //@formatter:off
        private final static List<String> list = Arrays.asList(
                "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController",
                "springfox.documentation.swagger.web.ApiResourceController",
                "springfox.documentation.swagger2.web.Swagger2ControllerWebMvc",
                "org.springdoc.webmvc.ui.SwaggerConfigResource",
                "org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource"
        );
        //@formatter:on
        /**
         * 是否开启响应增强功能配置，默认为开启
         */
        private Boolean enable = false;

        /**
         * 不进行响应增加的接口对应的class的全量名
         */
        private List<String> excludes = list;

    }

}
