package com.yishuifengxiao.common.security.token.authentication;

import com.yishuifengxiao.common.security.token.SecurityToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * A holder of selected HTTP details related to a web authentication request.
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleWebAuthenticationDetails extends WebAuthenticationDetails {

    private SecurityToken token;

    public SimpleWebAuthenticationDetails(HttpServletRequest request, SecurityToken token) {
        super(request);
        this.token = token;
    }

    public SimpleWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
    }

    public SimpleWebAuthenticationDetails(String remoteAddress, String sessionId) {
        super(remoteAddress, sessionId);
    }

    public SecurityToken getToken() {
        return token;
    }
}
