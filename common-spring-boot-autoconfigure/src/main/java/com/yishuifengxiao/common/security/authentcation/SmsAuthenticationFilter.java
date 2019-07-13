package com.yishuifengxiao.common.security.authentcation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

/**
 * 模仿UsernamePasswordAuthenticationFilter实现自己的SmsCodeAuthenticationFilter
 * 
 * @author admin
 *
 */
public class SmsAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {
	// ~ Static fields/initializers
	// =====================================================================================

	public static final String MOBILE_KEY = "phone";

	private String moblileParameter = MOBILE_KEY;
	/**
	 * 是否值处理POST请求
	 */
	private boolean postOnly = true;

	// ~ Constructors
	// ===================================================================================================

	public SmsAuthenticationFilter(String url) {
		super(new AntPathRequestMatcher(url, "POST"));
	}

	// ~ Methods
	// ========================================================================================================
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !"POST".equalsIgnoreCase(request.getMethod())) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: "
							+ request.getMethod());
		}

		String mobile = obtainUsername(request);

		if (mobile == null) {
			mobile = "";
		}

		mobile = mobile.trim();

		SmsAuthenticationToken authRequest = new SmsAuthenticationToken(
				mobile);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	/**
	 * Enables subclasses to override the composition of the username, such as
	 * by including additional values and a separator.
	 *
	 * @param request
	 *            so that request attributes can be retrieved
	 *
	 * @return the username that will be presented in the
	 *         <code>Authentication</code> request token to the
	 *         <code>AuthenticationManager</code>
	 */
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(moblileParameter);
	}

	/**
	 * Provided so that subclasses may configure what is put into the
	 * authentication request's details property.
	 *
	 * @param request
	 *            that an authentication request is being created for
	 * @param authRequest
	 *            the authentication request object that should have its details
	 *            set
	 */
	protected void setDetails(HttpServletRequest request,
			SmsAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource
				.buildDetails(request));
	}

	/**
	 * Defines whether only HTTP POST requests will be allowed by this filter.
	 * If set to true, and an authentication request is received which is not a
	 * POST request, an exception will be raised immediately and authentication
	 * will not be attempted. The <tt>unsuccessfulAuthentication()</tt> method
	 * will be called as if handling a failed authentication.
	 * <p>
	 * Defaults to <tt>true</tt> but may be overridden by subclasses.
	 */
	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public final String getMoblileParameter() {
		return moblileParameter;
	}

	public void setMoblileParameter(String moblileParameter) {
		Assert.hasText(moblileParameter,
				"Mobile parameter must not be empty or null");
		this.moblileParameter = moblileParameter;
	}

}