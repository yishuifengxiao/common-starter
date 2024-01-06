package com.yishuifengxiao.common.security.user.userdetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义UserDetailsService实现类，查找用户
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class CustomeUserDetailsServiceImpl implements UserDetailsService {

    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 不应该在这里加密，数据库里就应该存的是的加密后的密文
        String encodePassword = passwordEncoder.encode("12345678");
        log.trace("自定义UserDetailsService实现类中获取到的用户名为 {} ", username);

        // 这里不比较密码的正确性，在返回后由spring security比较密码正确性
        // 在有需要获取当前用户的地方可以用户 @CurrentUser CustomUser currentUser 获取当前用户
        //例如：
//        @GetMapping("/user")
//        public CustomUser user(@CurrentUser CustomUser currentUser) {
//            return currentUser;
//        }

        return new CustomUserDetails(new CustomUser(), username, encodePassword, true, true, true, true,
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
