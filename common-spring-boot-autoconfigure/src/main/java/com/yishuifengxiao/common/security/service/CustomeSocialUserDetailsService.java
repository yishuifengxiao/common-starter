package com.yishuifengxiao.common.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * 自定义SocialUserDetailsService实现类，查找用户
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年6月23日
 */
public class CustomeSocialUserDetailsService implements SocialUserDetailsService {

	private final static Logger log = LoggerFactory.getLogger(CustomeUserDetailsServiceImpl.class);

	private PasswordEncoder passwordEncoder;
	
	/**
	 * spring social中的方法
	 */
	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		log.debug("============================> spring social 得到的用户id为 {}",userId);
		return new SocialUser("yishuifengxiao", passwordEncoder.encode("12345678"), true, true, true, true,
				AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_USER"));
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	
	
}
