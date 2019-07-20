package com.yishuifengxiao.common.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义UserDetailsService实现类，查找用户
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年6月23日
 */
public class CustomeUserDetailsServiceImpl implements UserDetailsService {
	private final static Logger log = LoggerFactory.getLogger(CustomeUserDetailsServiceImpl.class);

	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 不应该在这里加密，数据库里就应该存的是的加密后的密文
		String encodePassword = passwordEncoder.encode("12345678");
		log.info("自定义UserDetailsService实现类中获取到的用户名为 {} ,得到的数据库密码(已加密的密码)为 {}", username, encodePassword);

		// 这里不比较密码的正确性，在返回后由spring security比较密码正确性
		return new User(username, encodePassword, true, true, true, true,
				AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_USER"));
	}

	public CustomeUserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}


}
