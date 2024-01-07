package com.yishuifengxiao.common.security.utils;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * UserDetails 状态检查
 *
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/7 15:21
 * @since 1.0.0
 */
@Slf4j
public class SimepleUserDetailsChecker implements UserDetailsChecker, MessageSourceAware {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private SecurityPropertyResource securityPropertyResource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public void check(UserDetails userDetails) {


        if (null == userDetails) {
            throw new UncheckedException(ErrorCode.USERNAME_NO_EXTIS,
                    securityPropertyResource.security().getMsg().getAccountNoExtis());
        }
        if (BooleanUtils.isFalse(userDetails.isAccountNonExpired())) {
            throw new UncheckedException(ErrorCode.ACCOUNT_EXPIRED,
                    securityPropertyResource.security().getMsg().getAccountExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonLocked())) {
            throw new UncheckedException(ErrorCode.ACCOUNT_LOCKED,
                    securityPropertyResource.security().getMsg().getAccountLocked());
        }

        if (BooleanUtils.isFalse(userDetails.isCredentialsNonExpired())) {
            throw new UncheckedException(ErrorCode.PASSWORD_EXPIRED,
                    securityPropertyResource.security().getMsg().getPasswordExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isEnabled())) {
            throw new UncheckedException(ErrorCode.ACCOUNT_UNENABLE,
                    securityPropertyResource.security().getMsg().getAccountNoEnable());
        }
    }

    public SimepleUserDetailsChecker() {
    }

    public SimepleUserDetailsChecker(SecurityPropertyResource securityPropertyResource) {
        this.securityPropertyResource = securityPropertyResource;
    }

    public SecurityPropertyResource getSecurityPropertyResource() {
        return securityPropertyResource;
    }

    public void setSecurityPropertyResource(SecurityPropertyResource securityPropertyResource) {
        this.securityPropertyResource = securityPropertyResource;
    }
}


