package com.yishuifengxiao.common.security.token;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeHelper;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.lang.CompareUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * 系统安全信息处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleTokenHelper implements TokenHelper {

    private PropertyResource propertyResource;


    private PasswordEncoder passwordEncoder;
    private AuthorizeHelper authorizeHelper;
    /**
     * token生成器
     */
    private TokenBuilder tokenBuilder;


    @Override
    public SecurityToken createUnsafe(String username, String deviceId) throws CustomException {

        return this.createUnsafe(username, deviceId, propertyResource.security().getToken().getValidSeconds());
    }

    @Override
    public SecurityToken createUnsafe(String username, String deviceId, int validSeconds) throws CustomException {
        if (StringUtils.isBlank(username)) {
            throw new CustomException("账号不能为空");
        }

        UserDetails userDetails = authorizeHelper.loadUserByUsername(username.trim());
        return this.create(userDetails, deviceId, validSeconds,
                propertyResource.security().getToken().getPreventsLogin(),
                propertyResource.security().getToken().getMaxSessions());

    }

    @Override
    public SecurityToken create(String username, String deviceId, String password) throws CustomException {
        if (StringUtils.isBlank(username)) {
            throw new CustomException("账号不能为空");
        }

        if (StringUtils.isBlank(password)) {
            password = "";
        }

        UserDetails userDetails = authorizeHelper.loadUserByUsername(username.trim());

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_ERROR,
                    propertyResource.security().getMsg().getPasswordIsError());
        }

        return this.create(userDetails, deviceId, propertyResource.security().getToken().getValidSeconds(),
                propertyResource.security().getToken().getPreventsLogin(),
                propertyResource.security().getToken().getMaxSessions());
    }


    /**
     * <p>
     * 根据指定参数生成访问令牌
     * </p>
     *
     * @param userDetails   用户认证信息
     * @param deviceId      设备id
     * @param validSeconds  令牌过期时间，单位为秒
     * @param preventsLogin 在达到同一个账号最大的登陆数量时是否阻止后面的用户登陆,默认为false
     * @param maxSessions   同一个账号最大的登陆数量
     * @return 访问令牌
     * @throws CustomException 创建时发生问题
     */
    private SecurityToken create(UserDetails userDetails, String deviceId, int validSeconds, boolean preventsLogin,
                                 int maxSessions) throws CustomException {

        if (null == userDetails) {
            throw new CustomException(ErrorCode.NO_USERDETAILS,
                    propertyResource.security().getMsg().getUserDetailsIsNull());
        }


        if (!CompareUtil.gtZero(validSeconds)) {
            validSeconds = TokenConstant.TOKEN_VALID_TIME_IN_SECOND;
        }

        if (!CompareUtil.gtZero(maxSessions)) {
            maxSessions = TokenConstant.MAX_SESSION_NUM;
        }

        // 检查用户信息
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        // 根据用户信息生成一个访问令牌
        SecurityToken token = tokenBuilder.creatNewToken(authentication.getName(), deviceId, validSeconds,
                preventsLogin, maxSessions, userDetails.getAuthorities());
        token.setDetails(userDetails);

        // 将认证信息注入到spring Security中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;

    }

    /**
     * <p>清除指定账号下面的所有的令牌</p>
     * <p>一般用于用户修改密码后使用</p>
     *
     * @param username 用户账号
     */
    @Override
    public void clear(String username) throws CustomException {
        final List<SecurityToken> tokens = tokenBuilder.loadAllToken(username);
        if (null == tokens) {
            return;
        }
        for (SecurityToken token : tokens) {
            tokenBuilder.remove(token);
        }
    }

    public SimpleTokenHelper(PropertyResource propertyResource, AuthorizeHelper authorizeHelper, PasswordEncoder passwordEncoder, TokenBuilder tokenBuilder) {
        this.propertyResource = propertyResource;
        this.authorizeHelper = authorizeHelper;
        this.tokenBuilder = tokenBuilder;
        this.passwordEncoder = passwordEncoder;
    }
}
