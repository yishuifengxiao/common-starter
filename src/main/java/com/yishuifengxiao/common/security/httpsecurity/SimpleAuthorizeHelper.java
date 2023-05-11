package com.yishuifengxiao.common.security.httpsecurity;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.TokenExpireEvent;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private PasswordEncoder passwordEncoder;

    /**
     * token生成器
     */
    private TokenBuilder tokenBuilder;


    @Override
    public Authentication authorize(String tokenValue) throws CustomException {
        if (StringUtils.isBlank(tokenValue)) {
            throw new CustomException(propertyResource.security().getMsg().getTokenValueIsNull());
        }

        // 解析token
        SecurityToken token = tokenBuilder.loadByTokenValue(tokenValue);

        if (propertyResource.showDetail()) {
            log.info("【yishuifengxiao-common-spring-boot-starter】根据访问令牌 {} 获取到的认证信息为 {}", tokenValue, token);
        }

        CustomException e = null;

        if (null == token) {
            e = new CustomException(ErrorCode.INVALID_TOKEN, propertyResource.security().getMsg().getTokenIsNull());

            SpringContext.publishEvent(new TokenExpireEvent(this, e, token, tokenValue));

            throw e;
        }

        if (token.isExpired()) {
            if (propertyResource.showDetail()) {
                log.info("【yishuifengxiao-common-spring-boot-starter】访问令牌 {} 已过期 ", token);
            }
            // 删除失效的token
            tokenBuilder.remove(token);

            e = new CustomException(ErrorCode.EXPIRED_ROKEN, propertyResource.security().getMsg().getTokenIsExpired());

            SpringContext.publishEvent(new TokenExpireEvent(this, e, token, tokenValue));
            throw e;
        }

        if (!token.isActive()) {
            if (propertyResource.showDetail()) {
                log.info("【yishuifengxiao-common-spring-boot-starter】访问令牌 {} 已失效 ", token);
            }
            // 删除失效的token
            tokenBuilder.remove(token);

            e = new CustomException(ErrorCode.EXPIRED_ROKEN, propertyResource.security().getMsg().getTokenIsInvalid());

            SpringContext.publishEvent(new TokenExpireEvent(this, e, token, tokenValue));
            throw e;
        }

        // 获取认证状态
        UserDetails userDetails = this.loadUserByUsername(token.getUsername());

        // 刷新令牌的过期时间
        tokenBuilder.refreshExpireTime(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        return authentication;
    }

    @Override
    public UserDetails authorize(String username, String password) throws CustomException {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_ERROR,
                    propertyResource.security().getMsg().getPasswordIsError());
        }

        return userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws CustomException {
        if (StringUtils.isBlank(username)) {
            throw new CustomException("账号不能为空");
        }
        // 获取认证信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username.trim());

        if (null == userDetails) {
            throw new CustomException(ErrorCode.USERNAME_NO_EXTIS,
                    propertyResource.security().getMsg().getAccountNoExtis());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonExpired())) {
            throw new CustomException(ErrorCode.ACCOUNT_EXPIRED,
                    propertyResource.security().getMsg().getAccountExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonLocked())) {
            throw new CustomException(ErrorCode.ACCOUNT_LOCKED, propertyResource.security().getMsg().getAccountLocked());
        }

        if (BooleanUtils.isFalse(userDetails.isCredentialsNonExpired())) {
            throw new CustomException(ErrorCode.PASSWORD_EXPIRED,
                    propertyResource.security().getMsg().getPasswordExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isEnabled())) {
            throw new CustomException(ErrorCode.ACCOUNT_UNENABLE,
                    propertyResource.security().getMsg().getAccountNoEnable());
        }
        return userDetails;
    }

    public SimpleAuthorizeHelper(PropertyResource propertyResource, UserDetailsService userDetailsService,
                                 PasswordEncoder passwordEncoder, TokenBuilder tokenBuilder) {
        this.propertyResource = propertyResource;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBuilder = tokenBuilder;
    }

}
