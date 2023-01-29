/**
 * 
 */
package com.yishuifengxiao.common.security.thirdauth.sms;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 根据手机号获取认证信息
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SmsUserDetailsService {
	// ~ Methods
	// ========================================================================================================

	/**
	 * Locates the user based on the username. In the actual implementation, the
	 * search may possibly be case sensitive, or case insensitive depending on how
	 * the implementation instance is configured. In this case, the
	 * <code>UserDetails</code> object that comes back may have a username that is
	 * of a different case than what was actually requested..
	 *
	 * @param username the username identifying the user whose data is required.
	 *
	 * @return a fully populated user record (never <code>null</code>)
	 *
	 * @throws UsernameNotFoundException if the user could not be found or the user
	 *                                   has no GrantedAuthority
	 */
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
