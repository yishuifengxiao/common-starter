package com.yishuifengxiao.common.security.filter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yishuifengxiao.common.security.entity.TokenInfo;
import com.yishuifengxiao.common.security.event.TokenEndpointEvent;
import com.yishuifengxiao.common.security.processor.impl.DefaultHandlerProcessor;
import com.yishuifengxiao.common.tool.entity.Response;

/**
 * 获取token时在BasicAuthenticationFilter之前增加一个过滤器
 * 
 * @author yishui
 * @date 2019年10月11日
 * @version 1.0.0
 */
public class TokenEndpointAuthenticationFilter extends OncePerRequestFilter implements InitializingBean {
	private final static Logger logger = LoggerFactory.getLogger(TokenEndpointAuthenticationFilter.class);


	private ApplicationContext contentx;

	private ClientDetailsService clientDetailsService;

	private PasswordEncoder passwordEncoder;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		
		String username = request.getParameter("username");

		HttpSession session = httpServletRequest.getSession();
		String sessionId = session.getId();
		String header = request.getHeader("Authorization");

		if (header == null || !header.toLowerCase().startsWith("basic ")) {
			chain.doFilter(request, response);
			return;
		}
		String[] tokens = extractAndDecodeHeader(header, request);

		String clientId = tokens[0];

		logger.debug("Basic Authentication Authorization header found for user '" + clientId + "'");

		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

		if (clientDetails == null) {

			onUnsuccessfulAuthentication(request, response, MessageFormat.format("终端{0}不存在", clientId));
			return;
		}
		if (!passwordEncoder.matches(tokens[1], clientDetails.getClientSecret())) {

			onUnsuccessfulAuthentication(request, response, MessageFormat.format("终端{0}对应的密码错误", clientId));
			return;
		}
		
		

		// 保存登陆信息
		TokenInfo tokenInfo = new TokenInfo(username, clientId, sessionId);
		// infoStore.saveTokenInfo(tokenInfo);
		// 发送消息
		contentx.publishEvent(new TokenEndpointEvent(tokenInfo));

		chain.doFilter(request, response);
	}

	@SuppressWarnings("rawtypes")
	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, String msg)
			throws IOException {
		Response data = Response.error(msg);
		new DefaultHandlerProcessor().send(request, response, data);
	}

	/**
	 * Decodes the header into a username and password.
	 *
	 * @throws BadCredentialsException if the Basic header is not present or is not
	 *                                 valid Base64
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new BadCredentialsException("token解析失败");
		}

		String token = new String(decoded, "utf-8");

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BadCredentialsException("无效的basic token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	@Override
	public void afterPropertiesSet() throws ServletException {
		// Assert.notNull(infoStore, "infoStore不能为空");

		Assert.notNull(clientDetailsService, "clientDetailsService不能为空");

		Assert.notNull(passwordEncoder, "passwordEncoder不能为空");

		Assert.notNull(contentx, "contentx不能为空");
	}

	public ApplicationContext getContentx() {
		return contentx;
	}

	public void setContentx(ApplicationContext contentx) {
		this.contentx = contentx;
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

	public TokenEndpointAuthenticationFilter() {

	}

}
