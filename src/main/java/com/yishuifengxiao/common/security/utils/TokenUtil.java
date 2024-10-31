package com.yishuifengxiao.common.security.utils;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.lang.CompareUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * 令牌生成工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenUtil {

    private static SecurityPropertyResource securityPropertyResource;


    private static PasswordEncoder passwordEncoder;

    private static UserDetailsService userDetailsService;
    /**
     * token生成器
     */
    private static TokenBuilder tokenBuilder;

    private static SecurityValueExtractor securityValueExtractor;
    private static UserDetailsChecker userDetailsChecker;

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @param password 账号对应的密码
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(String username, String password) throws CustomException {
        Assert.isNotBlank("密码不能为空", password);
        return create(username, null, password, null, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param request  HttpServletRequest
     * @param username 用户账号
     * @param password 账号对应的密码
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(HttpServletRequest request, String username, String password) throws CustomException {
        Assert.isNotBlank("密码不能为空", password);
        String deviceId = securityValueExtractor.extractDeviceId(request, null);
        return create(username, deviceId, password, null, null, null);
    }


    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @param password 账号对应的密码
     * @param deviceId 设备id
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(String username, String deviceId, String password) throws CustomException {
        Assert.isNotBlank("密码不能为空", password);
        return create(username, deviceId, password, null, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param username     用户账号
     * @param password     账号对应的密码
     * @param deviceId     设备id
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(String username, String deviceId, String password, Integer validSeconds) throws CustomException {
        Assert.isNotBlank("密码不能为空", password);
        return create(username, deviceId, password, validSeconds, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username) throws CustomException {

        return create(username, null, null, null, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param request  HttpServletRequest
     * @param username 用户账号
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(HttpServletRequest request, String username) throws CustomException {
        String deviceId = securityValueExtractor.extractDeviceId(request, null);
        return create(username, deviceId, null, null, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param username     用户账号
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username, int validSeconds) throws CustomException {
        return create(username, null, null, validSeconds, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param request      HttpServletRequest
     * @param username     用户账号
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(HttpServletRequest request, String username, int validSeconds) throws CustomException {
        String deviceId = securityValueExtractor.extractDeviceId(request, null);
        return create(username, deviceId, null, validSeconds, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username, String deviceId) throws CustomException {

        return create(username, deviceId, null, null, null, null);
    }

    /**
     * 生成一个令牌
     *
     * @param username     用户账号
     * @param deviceId     设备id
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username, String deviceId, int validSeconds) throws CustomException {

        return create(username, deviceId, null, validSeconds, null, null);
    }


    /**
     * <p>
     * 根据指定参数生成访问令牌
     * </p>
     *
     * @param username      用户名
     * @param deviceId      设备id
     * @param password      用户密码
     * @param validSeconds  令牌过期时间，单位为秒
     * @param preventsLogin 在达到同一个账号最大的登陆数量时是否阻止后面的用户登陆,默认为false
     * @param maxSessions   同一个账号最大的登陆数量
     * @return 访问令牌
     * @throws CustomException 创建时发生问题
     */
    private static SecurityToken create(String username, String deviceId, String password, Integer validSeconds,
                                        Boolean preventsLogin, Integer maxSessions) throws CustomException {

        UserDetails userDetails = loadUserByUsername(username.trim());

        if (null == userDetails) {
            throw new CustomException(ErrorCode.NO_USERDETAILS,
                    TokenUtil.securityPropertyResource.security().getMsg().getUserDetailsIsNull());
        }
        if (null != password && !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_ERROR,
                    securityPropertyResource.security().getMsg().getPasswordIsError());
        }


        if (null == validSeconds || CompareUtil.lteZero(validSeconds)) {
            validSeconds = securityPropertyResource.security().getToken().getValidSeconds();
        }

        if (null == maxSessions || CompareUtil.lteZero(maxSessions)) {
            maxSessions = securityPropertyResource.security().getToken().getMaxSessions();
        }
        if (null == preventsLogin) {
            preventsLogin = securityPropertyResource.security().getToken().getPreventsLogin();
        }

        // 检查用户信息
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());

        // 根据用户信息生成一个访问令牌
        SecurityToken token = TokenUtil.tokenBuilder.createNewToken(authentication, deviceId, validSeconds,
                preventsLogin, maxSessions, userDetails.getAuthorities());

        return token;

    }

    private static UserDetails loadUserByUsername(String username) throws CustomException {
        if (StringUtils.isBlank(username)) {
            throw new CustomException("账号不能为空");
        }
        // 获取认证信息
        UserDetails userDetails = TokenUtil.userDetailsService.loadUserByUsername(username);
        TokenUtil.userDetailsChecker.check(userDetails);

        return userDetails;
    }

    /**
     * 删除指定账号下所有的令牌
     *
     * @param authentication 用户认证信息
     */
    public static void clearAuthentication(Authentication authentication) {
        TokenUtil.tokenBuilder.clearAll(authentication);
    }


    @SuppressWarnings("unused")
    private TokenUtil() {
    }

    public TokenUtil(SecurityPropertyResource securityPropertyResource, PasswordEncoder passwordEncoder,
                     UserDetailsService userDetailsService, TokenBuilder tokenBuilder,
                     SecurityValueExtractor securityValueExtractor) {

        TokenUtil.securityPropertyResource = securityPropertyResource;
        TokenUtil.passwordEncoder = passwordEncoder;
        TokenUtil.userDetailsService = userDetailsService;
        TokenUtil.tokenBuilder = tokenBuilder;
        TokenUtil.securityValueExtractor = securityValueExtractor;
        TokenUtil.userDetailsChecker = new SimepleUserDetailsChecker(securityPropertyResource);

    }


}
