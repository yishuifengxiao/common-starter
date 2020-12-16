package com.yishuifengxiao.common.security.authentcation;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 实现自定义SmsCodeAuthenticationProvider
 * 
 * @author admin
 *
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {
	private UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		SmsAuthenticationToken smsCodeAuthenticationToken = (SmsAuthenticationToken) authentication;
		
		UserDetails userDetails = userDetailsService
				.loadUserByUsername((String) smsCodeAuthenticationToken.getPrincipal());
		
		if (userDetails == null) {
			throw new InternalAuthenticationServiceException("无法获取用户信息");
		}
		
		SmsAuthenticationToken smsCodeAuthenticationResult = new SmsAuthenticationToken(
				(String) smsCodeAuthenticationToken.getPrincipal(), userDetails.getAuthorities());
		
		smsCodeAuthenticationResult.setDetails(smsCodeAuthenticationToken.getDetails());
		return smsCodeAuthenticationResult;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// 确定是否调用这个方法
		return SmsAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}