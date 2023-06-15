package com.yishuifengxiao.common.security.token;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.lang.CompareUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 令牌生成工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenUtil {

    private static PropertyResource propertyResource;


    private static PasswordEncoder passwordEncoder;

    private static UserDetailsService userDetailsService;
    /**
     * token生成器
     */
    private static TokenBuilder tokenBuilder;

    private static SecurityValueExtractor securityValueExtractor;

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
                    TokenUtil.propertyResource.security().getMsg().getUserDetailsIsNull());
        }
        if (null != password && !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_ERROR,
                    propertyResource.security().getMsg().getPasswordIsError());
        }


        if (null == validSeconds || CompareUtil.lteZero(validSeconds)) {
            validSeconds = propertyResource.security().getToken().getValidSeconds();
        }

        if (null == maxSessions || CompareUtil.lteZero(maxSessions)) {
            maxSessions = propertyResource.security().getToken().getMaxSessions();
        }
        if (null == preventsLogin) {
            preventsLogin = propertyResource.security().getToken().getPreventsLogin();
        }

        // 检查用户信息
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());

        // 根据用户信息生成一个访问令牌
        SecurityToken token = TokenUtil.tokenBuilder.creatNewToken(authentication, deviceId, validSeconds,
                preventsLogin, maxSessions, userDetails.getAuthorities());

        // 将认证信息注入到spring Security中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;

    }

    private static UserDetails loadUserByUsername(String username) throws CustomException {
        if (StringUtils.isBlank(username)) {
            throw new CustomException("账号不能为空");
        }
        // 获取认证信息
        UserDetails userDetails = TokenUtil.userDetailsService.loadUserByUsername(username);

        if (null == userDetails) {
            throw new CustomException(ErrorCode.USERNAME_NO_EXTIS,
                    propertyResource.security().getMsg().getAccountNoExtis());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonExpired())) {
            throw new CustomException(ErrorCode.ACCOUNT_EXPIRED,
                    propertyResource.security().getMsg().getAccountExpired());
        }

        if (BooleanUtils.isFalse(userDetails.isAccountNonLocked())) {
            throw new CustomException(ErrorCode.ACCOUNT_LOCKED,
                    propertyResource.security().getMsg().getAccountLocked());
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

    /**
     * <p>删除指定账号下所有的令牌</p>
     * <p style="color:red;">支持多终端登录的情况下该操作会导致所有的登录会话全部失效</p>
     *
     * @param authentication 用户认证信息
     */
    public static void clearAllToken(Authentication authentication) {
        TokenUtil.tokenBuilder.clearAll(authentication);
    }


    /**
     * 删除指定的令牌
     *
     * @param token 待删除的令牌
     */
    public static void removeToken(SecurityToken token) {
        TokenUtil.tokenBuilder.remove(token);
    }

    /**
     * 根据认证信息获取所有的令牌
     *
     * @param authentication 认证信息
     * @return 获取的令牌
     */
    public static List<SecurityToken> loadAllToken(Authentication authentication) {
        return TokenUtil.tokenBuilder.loadAll(authentication);
    }

    /**
     * 清除该会话中的认证信息
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication Authentication
     */
    public static void clearAuthentication(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) {
        List<SecurityToken> list = TokenUtil.tokenBuilder.loadAll(authentication);
        String deviceId = securityValueExtractor.extractDeviceId(request, response);
        list.stream().filter(v -> v.getDeviceId().equals(deviceId)).forEach(TokenUtil.tokenBuilder::remove);
    }

    @SuppressWarnings("unused")
    private TokenUtil() {
    }

    public TokenUtil(PropertyResource propertyResource, PasswordEncoder passwordEncoder,
                     UserDetailsService userDetailsService, TokenBuilder tokenBuilder,
                     SecurityValueExtractor securityValueExtractor) {

        TokenUtil.propertyResource = propertyResource;
        TokenUtil.passwordEncoder = passwordEncoder;
        TokenUtil.userDetailsService = userDetailsService;
        TokenUtil.tokenBuilder = tokenBuilder;
        TokenUtil.securityValueExtractor = securityValueExtractor;

    }


}
