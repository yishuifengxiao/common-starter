/**
 *
 */
package com.yishuifengxiao.common.oauth2.filter;

import com.yishuifengxiao.common.oauth2.Oauth2Properties;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.utils.HttpExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 用于oauth2密码模式下载提前校验用户名和密码是否正确
 * </p>
 * 在 <code>Oauth2Server</code>中被使用
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
@Slf4j
public class TokenEndpointFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher MATCHER = new AntPathRequestMatcher("/oauth/token");

    private HttpExtractor httpExtractor = new HttpExtractor();

    private final static String BASIC = "basic ";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String GRANT_TYPE = "grant_type";

    private static final String PARAM_VALUE = "password";


    /**
     * 协助处理器
     */
    private SecurityHandler securityHandler;

    private PropertyResource propertyResource;

    private SecurityHelper securityHelper;

    private ClientDetailsService clientDetailsService;

    private PasswordEncoder passwordEncoder;

    private Oauth2Properties oauth2Properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (MATCHER.matches(httpServletRequest)) {

            String header = request.getHeader("Authorization");

            if (header == null || !header.toLowerCase().startsWith(BASIC)) {
                chain.doFilter(request, response);
                return;
            }

            try {
                String[] tokens = httpExtractor.extractBasicAuth(request);

                String clientId = tokens[0];

                if (propertyResource.showDetail()) {
                    log.info("Basic Authentication Authorization header found for user '" + clientId + "'");
                }

                ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

                if (clientDetails == null) {
                    // 终端不存在
                    throw new CustomException(oauth2Properties.getClientNotExtis());
                }
                if (!passwordEncoder.matches(tokens[1], clientDetails.getClientSecret())) {
                    // 密码错误
                    throw new CustomException(oauth2Properties.getPwdErrorMsg());
                }

                // 授权类型
                String grantType = httpServletRequest.getParameter(GRANT_TYPE);
                if (StringUtils.containsIgnoreCase(PARAM_VALUE, grantType)) {
                    // 密码模式

                    String username = httpServletRequest.getParameter(USERNAME);
                    String password = httpServletRequest.getParameter(PASSWORD);
                    if (propertyResource.showDetail()) {
                        log.info("The user name obtained in oauth2 password mode is {} ", username);
                    }
                    securityHelper.authorize(username, password);
                }

            } catch (CustomException e) {
                securityHandler.preAuth(this.propertyResource, request, response,
                        Response.of(oauth2Properties.getInvalidClientCode(), e.getMessage(), null));
                return;
            } catch (Exception e) {
                // 其他异常
                securityHandler.onException(this.propertyResource, httpServletRequest, httpServletResponse, e);
                return;
            }

        }

        chain.doFilter(request, response);

    }

    public TokenEndpointFilter(
            SecurityHandler securityHandler,
            PropertyResource propertyResource,
            SecurityHelper securityHelper,
            ClientDetailsService clientDetailsService,
            PasswordEncoder passwordEncoder,
            Oauth2Properties oauth2Properties) {
        this.securityHandler = securityHandler;
        this.propertyResource = propertyResource;
        this.securityHelper = securityHelper;
        this.clientDetailsService = clientDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.oauth2Properties = oauth2Properties;
    }
}
