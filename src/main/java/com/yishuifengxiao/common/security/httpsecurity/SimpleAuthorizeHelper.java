package com.yishuifengxiao.common.security.httpsecurity;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.exception.AbnormalAccountException;
import com.yishuifengxiao.common.security.support.PropertyResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 系统安全信息处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleAuthorizeHelper implements AuthorizeHelper {

    private PropertyResource propertyResource;

    private UserDetailsService userDetailsService;


    @Override
    public UserDetails loadUserByUsername(String username) throws AbnormalAccountException {
        if (StringUtils.isBlank(username)) {
            throw new AbnormalAccountException("账号不能为空");
        }
        // 获取认证信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username.trim());

        if (null == userDetails) {
            throw new AbnormalAccountException(ErrorCode.USERNAME_NO_EXTIS,
                    propertyResource.security().getMsg().getAccountNoExtis());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonExpired())) {
            throw new AbnormalAccountException(ErrorCode.ACCOUNT_EXPIRED,
                    propertyResource.security().getMsg().getAccountExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonLocked())) {
            throw new AbnormalAccountException(ErrorCode.ACCOUNT_LOCKED, propertyResource.security().getMsg().getAccountLocked());
        }

        if (BooleanUtils.isFalse(userDetails.isCredentialsNonExpired())) {
            throw new AbnormalAccountException(ErrorCode.PASSWORD_EXPIRED,
                    propertyResource.security().getMsg().getPasswordExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isEnabled())) {
            throw new AbnormalAccountException(ErrorCode.ACCOUNT_UNENABLE,
                    propertyResource.security().getMsg().getAccountNoEnable());
        }
        return userDetails;
    }

    public SimpleAuthorizeHelper(PropertyResource propertyResource, UserDetailsService userDetailsService,
                                 PasswordEncoder passwordEncoder) {
        this.propertyResource = propertyResource;
        this.userDetailsService = userDetailsService;
    }

}
