package com.yishuifengxiao.common.oauth2.filter;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yishuifengxiao.common.oauth2.Oauth2Properties;
import com.yishuifengxiao.common.oauth2.Oauth2Server;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.tool.entity.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * 获取token时在BasicAuthenticationFilter之前增加一个过滤器<br/>
 * 为TokenEndpoint添加新的自定义身份验证筛选器。过滤器将设置在defaultBasicAuthenticationFilter的上游。<br/>
 * 该过滤器由<code>Oauth2Server</code>收集，然后经过<code> configure(AuthorizationServerSecurityConfigurer security)</code>配置到oauth2中
 * 
 * @see Oauth2Server
 * @author yishui
 * @date 2019年10月11日
 * @version 1.0.0
 */
@Slf4j
public class TokenEndpointAuthenticationFilter extends OncePerRequestFilter implements InitializingBean {

	private final static String BASIC = "basic ";

	private ClientDetailsService clientDetailsService;

	private PasswordEncoder passwordEncoder;

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	private Oauth2Properties oauth2Properties;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		String header = request.getHeader("Authorization");

		if (header == null || !header.toLowerCase().startsWith(BASIC)) {
			chain.doFilter(request, response);
			return;
		}

		try {
			String[] tokens = extractAndDecodeHeader(header, request);

			String clientId = tokens[0];

			log.debug("Basic Authentication Authorization header found for user '" + clientId + "'");
			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

			if (clientDetails == null) {
				// 终端不存在
				handlerProcessor.preAuth(httpServletRequest, response, Response
						.of(Response.Const.CODE_INTERNAL_SERVER_ERROR, oauth2Properties.getClientNotExtis(), null));
				return;
			}
			if (!passwordEncoder.matches(tokens[1], clientDetails.getClientSecret())) {

				// 密码错误
				handlerProcessor.preAuth(httpServletRequest, response, Response
						.of(Response.Const.CODE_INTERNAL_SERVER_ERROR, oauth2Properties.getPwdErrorMsg(), null));
				return;
			}
		} catch (Exception e) {
			// 其他异常
			handlerProcessor.exception(httpServletRequest, response, e);
			return;
		}

		chain.doFilter(request, response);
	}

	/**
	 * Decodes the header into a username and password.
	 *
	 * @throws Exception if the Basic header is not present or is not valid Base64
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws Exception {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new Exception("无效的basic token");
		}

		String token = new String(decoded, "utf-8");

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new Exception("无效的basic token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	@Override
	public void afterPropertiesSet() throws ServletException {

		Assert.notNull(clientDetailsService, "clientDetailsService不能为空");

		Assert.notNull(passwordEncoder, "passwordEncoder不能为空");

	}

	public ClientDetailsService getClientDetailsService() {
		return clientDetailsService;
	}

	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

	public Oauth2Properties getOauth2Properties() {
		return oauth2Properties;
	}

	public void setOauth2Properties(Oauth2Properties oauth2Properties) {
		this.oauth2Properties = oauth2Properties;
	}

}
