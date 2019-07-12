package com.yishuifengxiao.common.security.authentcation;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
/**
 * 模仿UsernamePasswordAuthenticationToken实现自定义的SmsCodeAuthenticationToken
 * @author admin
 *
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken implements java.io.Serializable{


	// ~ Instance fields
	// ================================================================================================

	/**
	 * 
	 */
	private static final long serialVersionUID = -1506897701981698420L;
	private final String  mobile;
	

	// ~ Constructors
	// ===================================================================================================

	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
	 * will return <code>false</code>.
	 *
	 */
	public SmsAuthenticationToken(String mobile) {
		super(null);
		this.mobile = mobile;
		setAuthenticated(false);
	}

	/**
	 * This constructor should only be used by <code>AuthenticationManager</code> or
	 * <code>AuthenticationProvider</code> implementations that are satisfied with
	 * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
	 * authentication token.
	 *
	 * @param mobile
	 * @param authorities
	 */
	public SmsAuthenticationToken(String mobile, 
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.mobile = mobile;
		super.setAuthenticated(true); // must use super, as we override
	}

	// ~ Methods
	// ========================================================================================================
    @Override
	public Object getCredentials() {
		return this.mobile;
	}
    @Override
	public Object getPrincipal() {
		return this.mobile;
	}
	
	
	

	public String getMobile() {
		return mobile;
	}
	
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
	}
}