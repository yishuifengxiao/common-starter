package com.yishuifengxiao.common.oauth2.support;

import com.yishuifengxiao.common.oauth2.Oauth2Properties;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class Oauth2SecurityGlobalEnhanceFilter extends OncePerRequestFilter {

    private RequestMatcher authorizationEnhanceEndpointMatcher;

    private Oauth2Properties oauth2Properties;
    private RegisteredClientRepository registeredClientRepository;
    private OAuth2AuthorizationConsentService authorizationConsentService;

    private AuthorizationServerSettings authorizationServerSettings;

    public Oauth2SecurityGlobalEnhanceFilter(Oauth2Properties oauth2Properties,
                                             RegisteredClientRepository registeredClientRepository,
                                             OAuth2AuthorizationConsentService authorizationConsentService,
                                             AuthorizationServerSettings authorizationServerSettings) {
        this.oauth2Properties = oauth2Properties;
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationConsentService = authorizationConsentService;
        this.authorizationEnhanceEndpointMatcher = new AntPathRequestMatcher(oauth2Properties.getConsentInfoPath());
        this.authorizationServerSettings = authorizationServerSettings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!authorizationEnhanceEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            HttpUtils.write(request, response, Response.unAuth("The current request requires identity authentication." +
                    " Please verify your identity first"));
            return;
        }
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        String scope = request.getParameter(OAuth2ParameterNames.SCOPE);
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        // Remove scopes that were already approved
        Set<String> scopesToApprove = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (null == registeredClient) {
            HttpUtils.write(request, response, Response.error("Wrong clientId value, corresponding client not found"));
            return;
        }
        OAuth2AuthorizationConsent currentAuthorizationConsent =
                this.authorizationConsentService.findById(registeredClient.getId(), authentication.getName());
        if (null == currentAuthorizationConsent) {
            HttpUtils.write(request, response, Response.error("Incorrect authentication information, please request " +
                    "authentication first"));
            return;
        }
        Set<String> authorizedScopes;
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        OAuth2Enhance enhance = new OAuth2Enhance(clientId, state, withDescription(scopesToApprove),
                withDescription(previouslyApprovedScopes), authentication.getName(),
                authorizationServerSettings.getAuthorizationEndpoint());
        HttpUtils.write(request, response, Response.sucData(enhance));
        return;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class OAuth2Enhance implements Serializable {

        private String clientId;

        private String state;

        private Set<ScopeWithDescription> scopes;

        private Set<ScopeWithDescription> previouslyApprovedScopes;

        private String principalName;

        private String authorizationEndpoint;
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));

        }
        return scopeWithDescriptions;
    }

    public static class ScopeWithDescription implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -7611280858999143294L;
        private static final String DEFAULT_DESCRIPTION =
                "UNKNOWN SCOPE - We cannot provide information about this " + "permission, use caution when granting "
                        + "this.";
        private static final Map<String, String> scopeDescriptions = new HashMap<>();

        static {
            scopeDescriptions.put(OidcScopes.PROFILE, "This application will be able to read your profile " +
                    "information" + ".");
            scopeDescriptions.put("message.read", "This application will be able to read your message.");
            scopeDescriptions.put("message.write",
                    "This application will be able to add new messages. It will also " + "be able to edit and delete "
                            + "existing messages.");
            scopeDescriptions.put("other.scope", "This is another scope example of a scope description.");
        }

        public final String scope;
        public final String description;

        ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
        }
    }
}
