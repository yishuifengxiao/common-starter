package com.yishuifengxiao.common.security.smsauth.sms;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 模仿UsernamePasswordAuthenticationFilter实现自己的SmsCodeAuthenticationFilter
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class SmsAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {



	public static final String MOBILE_KEY = "phone";
	/**
	 * 请求方法
	 */
	public static final String METHOD = "POST";

	private String mobileParameter = MOBILE_KEY;
	/**
	 * 是否值处理POST请求
	 */
	private boolean postOnly = true;



	public SmsAuthenticationFilter(String url) {
		super(new AntPathRequestMatcher(url, "POST"));
	}


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !METHOD.equalsIgnoreCase(request.getMethod())) {
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
		return request.getParameter(mobileParameter);
	}

	/**
	 * Provided so that subclasses may configure what is put into the
	 * user request's details property.
	 *
	 * @param request
	 *            that an user request is being created for
	 * @param authRequest
	 *            the user request object that should have its details
	 *            set
	 */
	protected void setDetails(HttpServletRequest request,
			SmsAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource
				.buildDetails(request));
	}

	
	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public final String getMobileParameter() {
		return mobileParameter;
	}

	public void setMobileParameter(String mobileParameter) {
		Assert.hasText(mobileParameter,
				"Mobile parameter must not be empty or null");
		this.mobileParameter = mobileParameter;
	}

}