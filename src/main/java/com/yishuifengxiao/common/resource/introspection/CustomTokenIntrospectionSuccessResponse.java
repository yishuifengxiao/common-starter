/*
 * oauth2-oidc-sdk
 *
 * Copyright 2012-2016, Connect2id Ltd and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.yishuifengxiao.common.resource.introspection;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.auth.X509CertificateConfirmation;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;

import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

/**
 * Token introspection success response.
 *
 * <p>
 * Related specifications:
 *
 * <ul>
 * <li>OAuth 2.0 Token Introspection (RFC 7662).
 * <li>OAuth 2.0 Mutual TLS Client Authentication and Certificate Bound Access
 * Tokens (draft-ietf-oauth2-mtls-15).
 * </ul>
 */
/**
 * <p>token探寻响应 </p>

 * 在<code>CustomOpaqueTokenIntrospector</code>中被配置
 * 
 * @see CustomOpaqueTokenIntrospector
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Immutable
public class CustomTokenIntrospectionSuccessResponse extends TokenIntrospectionResponse implements SuccessResponse {

	/**
	 * Builder for constructing token introspection success responses.
	 */
	public static class Builder {

		/**
		 * The parameters.
		 */
		private final JSONObject params = new JSONObject();

		/**
		 * Creates a new token introspection success response builder.
		 *
		 * @param active {@code true} if the token is active, else {@code false}.
		 */
		public Builder(final boolean active) {

			params.put("active", active);
		}

		/**
		 * Creates a new token introspection success response builder with the
		 * parameters of the specified response.
		 *
		 * @param response The response which parameters to use. Not {@code null}.
		 */
		public Builder(final CustomTokenIntrospectionSuccessResponse response) {

			params.putAll(response.params);
		}

		/**
		 * Sets the token scope.
		 *
		 * @param scope The token scope, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder scope(final Scope scope) {
			if (scope != null) {
				params.put("scope", scope.toString());
			} else {
				params.remove("scope");
			}
			return this;
		}

		/**
		 * Sets the identifier for the OAuth 2.0 userService that requested the token.
		 *
		 * @param clientID The userService identifier, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder clientID(final ClientID clientID) {
			if (clientID != null) {
				params.put("client_id", clientID.getValue());
			} else {
				params.remove("client_id");
			}
			return this;
		}

		/**
		 * Sets the username of the resource owner who authorised the token.
		 *
		 * @param username The username, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder username(final String username) {
			if (username != null) {
				params.put("username", username);
			} else {
				params.remove("username");
			}
			return this;
		}

		/**
		 * Sets the token type.
		 *
		 * @param tokenType The token type, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder tokenType(final AccessTokenType tokenType) {
			if (tokenType != null) {
				params.put("token_type", tokenType.getValue());
			} else {
				params.remove("token_type");
			}
			return this;
		}

		/**
		 * Sets the token expiration time.
		 *
		 * @param exp The token expiration time, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder expirationTime(final Date exp) {
			if (exp != null) {
				params.put("exp", DateUtils.toSecondsSinceEpoch(exp));
			} else {
				params.remove("exp");
			}
			return this;
		}

		/**
		 * Sets the token issue time.
		 *
		 * @param iat The token issue time, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder issueTime(final Date iat) {
			if (iat != null) {
				params.put("iat", DateUtils.toSecondsSinceEpoch(iat));
			} else {
				params.remove("iat");
			}
			return this;
		}

		/**
		 * Sets the token not-before time.
		 *
		 * @param nbf The token not-before time, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder notBeforeTime(final Date nbf) {
			if (nbf != null) {
				params.put("nbf", DateUtils.toSecondsSinceEpoch(nbf));
			} else {
				params.remove("nbf");
			}
			return this;
		}

		/**
		 * Sets the token subject.
		 *
		 * @param sub The token subject, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder subject(final Subject sub) {
			if (sub != null) {
				params.put("sub", sub.getValue());
			} else {
				params.remove("sub");
			}
			return this;
		}

		/**
		 * Sets the token audience.
		 *
		 * @param audList The token audience, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder audience(final List<Audience> audList) {
			if (audList != null) {
				params.put("aud", Audience.toStringList(audList));
			} else {
				params.remove("aud");
			}
			return this;
		}

		/**
		 * Sets the token issuer.
		 *
		 * @param iss The token issuer, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder issuer(final Issuer iss) {
			if (iss != null) {
				params.put("iss", iss.getValue());
			} else {
				params.remove("iss");
			}
			return this;
		}

		/**
		 * Sets the token identifier.
		 *
		 * @param jti The token identifier, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder jwtID(final JWTID jti) {
			if (jti != null) {
				params.put("jti", jti.getValue());
			} else {
				params.remove("jti");
			}
			return this;
		}

		/**
		 * Sets the userService X.509 certificate SHA-256 thumbprint, for a mutual TLS userService
		 * certificate bound access token. Corresponds to the {@code cnf.x5t#S256}
		 * claim.
		 *
		 * @param x5t The userService X.509 certificate SHA-256 thumbprint, {@code null} if
		 *            not specified.
		 *
		 * @return This builder.
		 */
		@Deprecated
		public Builder x509CertificateSHA256Thumbprint(final Base64URL x5t) {

			if (x5t != null) {
				JSONObject cnf;
				if (params.containsKey("cnf")) {
					cnf = (JSONObject) params.get("cnf");
				} else {
					cnf = new JSONObject();
					params.put("cnf", cnf);
				}
				cnf.put("x5t#S256", x5t.toString());
			} else if (params.containsKey("cnf")) {
				JSONObject cnf = (JSONObject) params.get("cnf");
				cnf.remove("x5t#S256");
				if (cnf.isEmpty()) {
					params.remove("cnf");
				}
			}

			return this;
		}

		/**
		 * Sets the userService X.509 certificate confirmation, for a mutual TLS userService
		 * certificate bound access token. Corresponds to the {@code cnf.x5t#S256}
		 * claim.
		 *
		 * @param cnf The userService X.509 certificate confirmation, {@code null} if not
		 *            specified.
		 *
		 * @return This builder.
		 */
		public Builder x509CertificateConfirmation(final X509CertificateConfirmation cnf) {

			if (cnf != null) {
				Map.Entry<String, JSONObject> param = cnf.toJWTClaim();
				params.put(param.getKey(), param.getValue());
			} else {
				params.remove("cnf");
			}
			return this;
		}

		/**
		 * Sets a custom parameter.
		 *
		 * @param name  The parameter name. Must not be {@code null}.
		 * @param value The parameter value. Should map to a JSON type. If {@code null}
		 *              not specified.
		 *
		 * @return This builder.
		 */
		public Builder parameter(final String name, final Object value) {
			if (value != null) {
				params.put(name, value);
			} else {
				params.remove(name);
			}
			return this;
		}

		/**
		 * Builds a new token introspection success response.
		 *
		 * @return The token introspection success response.
		 */
		public CustomTokenIntrospectionSuccessResponse build() {

			return new CustomTokenIntrospectionSuccessResponse(params);
		}
	}

	/**
	 * The parameters.
	 */
	private final JSONObject params;

	/**
	 * Creates a new token introspection success response.
	 *
	 * @param params The response parameters. Must contain at least the required
	 *               {@code active} parameter and not be {@code null}.
	 */
	public CustomTokenIntrospectionSuccessResponse(final JSONObject params) {
		this.params = params;
	}

	/**
	 * Returns the active status for the token. Corresponds to the {@code active}
	 * claim.
	 *
	 * @return {@code true} if the token is active, else {@code false}.
	 */
	public boolean isActive() {

		try {
			return JSONObjectUtils.getBoolean(params, "active", false);
		} catch (ParseException e) {
			// always false on error
			return false; 
		}
	}

	/**
	 * Returns the scope of the token. Corresponds to the {@code scope} claim.
	 *
	 * @return The token scope, {@code null} if not specified.
	 */
	public Scope getScope() {

		try {
			return Scope.parse(JSONObjectUtils.getStringList(params, "scope"));
		} catch (ParseException e) {
			return null;
		}
	}

	public Scope getAuthorities() {

		try {
			return Scope.parse(JSONObjectUtils.getStringList(params, "authorities"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the identifier of the OAuth 2.0 userService that requested the token.
	 * Corresponds to the {@code client_id} claim.
	 *
	 * @return The userService identifier, {@code null} if not specified.
	 */
	public ClientID getClientID() {

		try {
			return new ClientID(JSONObjectUtils.getString(params, "client_id"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the username of the resource owner who authorised the token.
	 * Corresponds to the {@code username} claim.
	 *
	 * @return The username, {@code null} if not specified.
	 */
	public String getUsername() {

		try {
			return JSONObjectUtils.getString(params, "username", null);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the access token type. Corresponds to the {@code token_type} claim.
	 *
	 * @return The token type, {@code null} if not specified.
	 */
	public AccessTokenType getTokenType() {

		try {
			return new AccessTokenType(JSONObjectUtils.getString(params, "token_type"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the token expiration time. Corresponds to the {@code exp} claim.
	 *
	 * @return The token expiration time, {@code null} if not specified.
	 */
	public Date getExpirationTime() {

		try {
			return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(params, "exp"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the token issue time. Corresponds to the {@code iat} claim.
	 *
	 * @return The token issue time, {@code null} if not specified.
	 */
	public Date getIssueTime() {

		try {
			return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(params, "iat"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the token not-before time. Corresponds to the {@code nbf} claim.
	 *
	 * @return The token not-before time, {@code null} if not specified.
	 */
	public Date getNotBeforeTime() {

		try {
			return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(params, "nbf"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the subject of the token, usually a machine-readable identifier of
	 * the resource owner who authorised the token. Corresponds to the {@code sub}
	 * claim.
	 *
	 * @return The token subject, {@code null} if not specified.
	 */
	public Subject getSubject() {

		try {
			return new Subject(JSONObjectUtils.getString(params, "sub"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the intended audience for the token. Corresponds to the {@code aud}
	 * claim.
	 *
	 * @return The token audience, {@code null} if not specified.
	 */
	public List<Audience> getAudience() {
		// Try string array first, then string
		try {
			return Audience.create(JSONObjectUtils.getStringList(params, "aud"));
		} catch (ParseException e) {
			try {
				return new Audience(JSONObjectUtils.getString(params, "aud")).toSingleAudienceList();
			} catch (ParseException e2) {
				return null;
			}
		}
	}

	/**
	 * Returns the token issuer. Corresponds to the {@code iss} claim.
	 *
	 * @return The token issuer, {@code null} if not specified.
	 */
	public Issuer getIssuer() {

		try {
			return new Issuer(JSONObjectUtils.getString(params, "iss"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the token identifier. Corresponds to the {@code jti} claim.
	 *
	 * @return The token identifier, {@code null} if not specified.
	 */
	public JWTID getJWTID() {

		try {
			return new JWTID(JSONObjectUtils.getString(params, "jti"));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the userService X.509 certificate SHA-256 thumbprint, for a mutual TLS
	 * userService certificate bound access token. Corresponds to the
	 * {@code cnf.x5t#S256} claim.
	 *
	 *
	 * @return The userService X.509 certificate SHA-256 thumbprint, {@code null} if not
	 *         specified.
	 */
	@Deprecated
	public Base64URL getX509CertificateSHA256Thumbprint() {

		try {
			JSONObject cnf = JSONObjectUtils.getJSONObject(params, "cnf", null);

			if (cnf == null) {
				return null;
			}

			String x5t = JSONObjectUtils.getString(cnf, "x5t#S256", null);

			if (x5t == null) {
				return null;
			}

			return new Base64URL(x5t);

		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the userService X.509 certificate confirmation, for a mutual TLS userService
	 * certificate bound access token. Corresponds to the {@code cnf.x5t#S256}
	 * claim.
	 *
	 * @return The userService X.509 certificate confirmation, {@code null} if not
	 *         specified.
	 */
	public X509CertificateConfirmation getX509CertificateConfirmation() {

		return X509CertificateConfirmation.parse(params);
	}

	/**
	 * Returns the string parameter with the specified name.
	 *
	 * @param name The parameter name. Must not be {@code null}.
	 *
	 * @return The parameter value, {@code null} if not specified or if parsing
	 *         failed.
	 */
	public String getStringParameter(final String name) {

		try {
			return JSONObjectUtils.getString(params, name, null);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the boolean parameter with the specified name.
	 *
	 * @param name The parameter name. Must not be {@code null}.
	 *
	 * @return The parameter value.
	 *
	 * @throws ParseException If the parameter isn't specified or parsing failed.
	 */
	public boolean getBooleanParameter(final String name) throws ParseException {

		return JSONObjectUtils.getBoolean(params, name);
	}

	/**
	 * Returns the number parameter with the specified name.
	 *
	 * @param name The parameter name. Must not be {@code null}.
	 *
	 * @return The parameter value, {@code null} if not specified or parsing failed.
	 */
	public Number getNumberParameter(final String name) {

		try {
			return JSONObjectUtils.getNumber(params, name, null);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the string list parameter with the specified name.
	 *
	 * @param name The parameter name. Must not be {@code null}.
	 *
	 * @return The parameter value, {@code null} if not specified or if parsing
	 *         failed.
	 */
	public List<String> getStringListParameter(final String name) {

		try {
			return JSONObjectUtils.getStringList(params, name, null);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the JSON object parameter with the specified name.
	 *
	 * @param name The parameter name. Must not be {@code null}.
	 *
	 * @return The parameter value, {@code null} if not specified or if parsing
	 *         failed.
	 */
	public JSONObject getJSONObjectParameter(final String name) {

		try {
			return JSONObjectUtils.getJSONObject(params, name, null);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Returns the underlying parameters.
	 *
	 * @return The parameters, as JSON object.
	 */
	public JSONObject getParameters() {

		return params;
	}

	/**
	 * Returns a JSON object representation of this token introspection success
	 * response.
	 *
	 * <p>
	 * Example JSON object:
	 *
	 * <pre>
	 * {
	 *  "active"          : true,
	 *  "client_id"       : "l238j323ds-23ij4",
	 *  "username"        : "jdoe",
	 *  "scope"           : "read write dolphin",
	 *  "sub"             : "Z5O3upPC88QrAjx00dis",
	 *  "aud"             : "https://protected.example.net/resource",
	 *  "iss"             : "https://server.example.com/",
	 *  "exp"             : 1419356238,
	 *  "iat"             : 1419350238,
	 *  "extension_field" : "twenty-seven"
	 * }
	 * </pre>
	 *
	 * @return The JSON object.
	 */
	public JSONObject toJSONObject() {

		return new JSONObject(params);
	}

	@Override
	public boolean indicatesSuccess() {

		return true;
	}

	@Override
	public HTTPResponse toHTTPResponse() {

		HTTPResponse httpResponse = new HTTPResponse(HTTPResponse.SC_OK);
		httpResponse.setContentType(CommonContentTypes.APPLICATION_JSON);
		httpResponse.setContent(params.toJSONString());
		return httpResponse;
	}

	/**
	 * Parses a token introspection success response from the specified JSON object.
	 *
	 * @param jsonObject The JSON object to parse. Must not be {@code null}.
	 *
	 * @return The token introspection success response.
	 *
	 * @throws ParseException If the JSON object couldn't be parsed to a token
	 *                        introspection success response.
	 */
	public static CustomTokenIntrospectionSuccessResponse parse(final JSONObject jsonObject) throws ParseException {

		try {
			return new CustomTokenIntrospectionSuccessResponse(jsonObject);
		} catch (IllegalArgumentException e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

	/**
	 * Parses an token introspection success response from the specified HTTP
	 * response.
	 *
	 * @param httpResponse The HTTP response. Must not be {@code null}.
	 *
	 * @return The token introspection success response.
	 *
	 * @throws ParseException If the HTTP response couldn't be parsed to a token
	 *                        introspection success response.
	 */
	public static CustomTokenIntrospectionSuccessResponse parse(final HTTPResponse httpResponse) throws ParseException {

		httpResponse.ensureStatusCode(HTTPResponse.SC_OK);
		JSONObject jsonObject = httpResponse.getContentAsJSONObject();
		return parse(jsonObject);
	}
}
