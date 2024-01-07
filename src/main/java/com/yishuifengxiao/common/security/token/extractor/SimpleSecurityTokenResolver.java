package com.yishuifengxiao.common.security.token.extractor;

import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;


import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>默认的系统令牌提取器</p>
 * <p>参考<code> org.springframework.security.oauth2server.server.oauth2resource.web.DefaultBearerTokenResolver </code></p>
 * <p>参见 HTTP 身份验证
 * <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Authorization">https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Authorization</a></p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityTokenResolver implements SecurityTokenResolver {

    private static final Pattern authorizationPattern = Pattern.compile("^xtoken (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    private static final String AUTHORIZATION_TYPE = "xtoken";


    @Override
    public String extractTokenValue(HttpServletRequest request, HttpServletResponse response,
                                    SecurityPropertyResource securityPropertyResource) {


        String tokenVal = resolveFromAuthorizationHeader(request, securityPropertyResource);
        if (StringUtils.isBlank(tokenVal)) {
            //从请求参数中获取
            tokenVal = resolveFromRequestParameters(request, securityPropertyResource);
        }
        if (StringUtils.isBlank(tokenVal)) {
            //从session参数中获取
            tokenVal = resolveFromHttpSession(request, securityPropertyResource);
        }

        if (StringUtils.isBlank(tokenVal)) {
            //从cookies中获取
            tokenVal = resolveFromCookies(request, securityPropertyResource);
        }
        return tokenVal;
    }

    /**
     * 从请求头里获取到tokenValue
     *
     * @param request          HttpServletRequest
     * @param securityPropertyResource 资源管理器
     * @return tokenValue
     */
    private String resolveFromAuthorizationHeader(HttpServletRequest request, SecurityPropertyResource securityPropertyResource) {
        String headerParameter = securityPropertyResource.security().getToken().getHeaderParameter();
        if (StringUtils.isBlank(headerParameter)) {
            headerParameter = TokenConstant.TOKEN_HEADER_PARAM;
        }
        String authorization = request.getHeader(headerParameter);
        if (!StringUtils.startsWithIgnoreCase(authorization, AUTHORIZATION_TYPE)) {
            return null;
        }
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group("token");
    }

    /**
     * 从请求参数里获取tokenValue
     *
     * @param request          HttpServletRequest
     * @param securityPropertyResource 资源管理器
     * @return tokenValue
     */
    private String resolveFromRequestParameters(HttpServletRequest request, SecurityPropertyResource securityPropertyResource) {
        String requestParameter = securityPropertyResource.security().getToken().getRequestParameter();
        if (StringUtils.isBlank(requestParameter)) {
            requestParameter = TokenConstant.TOKEN_REQUEST_PARAM;
        }
        String[] values = request.getParameterValues(requestParameter);
        if (values == null || values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }
        return null;
    }

    /**
     * 从session里获取tokenValue
     *
     * @param request          HttpServletRequest
     * @param securityPropertyResource 资源管理器
     * @return tokenValue
     */
    private String resolveFromHttpSession(HttpServletRequest request, SecurityPropertyResource securityPropertyResource) {
        String requestParameter = securityPropertyResource.security().getToken().getRequestParameter();
        if (StringUtils.isBlank(requestParameter)) {
            requestParameter = TokenConstant.TOKEN_REQUEST_PARAM;
        }
        final Object value = request.getSession().getAttribute(requestParameter);
        if (null == value || StringUtils.isBlank(value.toString())) {
            return null;
        }
        return value.toString();
    }

    /**
     * 从Cookie里获取tokenValue
     *
     * @param request          HttpServletRequest
     * @param securityPropertyResource 资源管理器
     * @return tokenValue
     */
    private String resolveFromCookies(HttpServletRequest request, SecurityPropertyResource securityPropertyResource) {
        String requestParameter = securityPropertyResource.security().getToken().getRequestParameter();
        if (StringUtils.isBlank(requestParameter)) {
            requestParameter = TokenConstant.TOKEN_REQUEST_PARAM;
        }
        final Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return null;
        }
        String cookieName = requestParameter;
        final Cookie cookie =
                Arrays.stream(cookies).filter(v -> StringUtils.equals(v.getName(), cookieName)).findFirst().orElse(null);
        if (null == cookie) {
            return null;
        }
        return cookie.getValue();
    }

}
