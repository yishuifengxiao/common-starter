package com.yishuifengxiao.common.validation.sender.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.entity.EmailCode;
import com.yishuifengxiao.common.validation.eunm.CodeType;
import com.yishuifengxiao.common.validation.sender.CodeSender;

/**
 * 默认的邮箱验证码发送器
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class EmailCodeSender implements CodeSender<EmailCode> {

	private final static Logger log = LoggerFactory.getLogger(EmailCodeSender.class);

	private JavaMailSender javaMailSender;
	/**
	 * 邮件发送者
	 */
	private String emailSender;

	private CodeProperties codeProperties;

	@Override
	public void send(String target, EmailCode emailCode, CodeType codeType) throws ValidateException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			// 发送方邮箱
			helper.setFrom(emailSender);
			// 接收方邮箱
			helper.setTo(target);
			// 主题
			helper.setSubject(codeProperties.getEmail().getTitle());
			// 生成邮件内容
			String content = buildContent(target, emailCode);
			// 内容
			helper.setText(content, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			log.info("发送邮件验证码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("验证码发送失败");
		}

	}

	/**
	 * 生成内容模板
	 * 
	 * @param target
	 * @param emailCode
	 * @return
	 * @throws IOException
	 */
	private String buildContent(String target, EmailCode emailCode) throws IOException {
		String expireAtTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(emailCode.getExpireTime());
		String now = DateTimeFormatter.ofPattern("yyyy年MM月dd").format(LocalDateTime.now());
		String content = MessageFormat.format(HTML_TEMPLATE, emailCode.getCode(), expireAtTime, now);

		return content;
	}

	public JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public String getEmailSender() {
		return emailSender;
	}

	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}

	public CodeProperties getCodeProperties() {
		return codeProperties;
	}

	public void setCodeProperties(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

	public EmailCodeSender() {

	}

	public EmailCodeSender(JavaMailSender javaMailSender, String emailSender, CodeProperties codeProperties) {
		this.javaMailSender = javaMailSender;
		this.emailSender = emailSender;
		this.codeProperties = codeProperties;
	}

	/**
	 * 内容模板
	 */
	private final static String HTML_TEMPLATE = "<body tabindex='0' role='listitem'>"
			+ "    <table border='0' cellspacing='0' cellpadding='0' width='800' bgcolor='#0092ff' height='66'>"
			+ "        <tbody>" + "            <tr>" + "                <td width='50'></td>"
			+ "                <td width='750'><img style='WIDTH: 135px' src='http://static.yishuifengxiao.com/common/mail_logo.png'>"
			+ "                </td>" + "            </tr>" + "        </tbody>" + "    </table>"
			+ "    <table style='FONT-FAMILY: 黑体; FONT-SIZE: 10pt' border='0' cellspacing='0' cellpadding='50' width='800'>"
			+ "        <tbody>" + "            <tr>" + "                <td width='800'>" + "                    <div>"
			+ "                        <div style='FONT-SIZE: 11pt'>" + "                            亲爱的用户，您好！"
			+ "                        </div>" + "                        <br>"
			+ "                        <div style='FONT-SIZE: 11pt'>" + "                            您本次请求创建的验证码为："
			+ "                        </div>" + "                        <br>" + "                        <br>"
			+ "                        <div>"
			+ "                            <span style='COLOR:black;FONT-SIZE:40px;font-weight:bold;'>{0}</span><br/>"
			+ "                            <span style='COLOR:red;FONT-SIZE:11px'>(为了保障您帐号的安全性，请在  {1}  之前完成验证。)</span>"
			+ "                        </div>" + "                        <br>" + "                        <br>"
			+ "                        <hr"
			+ "                            style='BORDER-BOTTOM:#808080 0px dashed; BORDER-LEFT:#808080 0px dashed;HEIGHT:1px;BORDER-TOP:#808080 1px dashed;BORDER-RIGHT:#808080 0px dashed'>"
			+ "                        <br>" + "                        <div style='COLOR: #808080'>"
			+ "                            此邮件由系统自动发出，系统不接受回信，因此请勿直接回复。"
			+ "                            <br> 安全使用您的<a href='www.yishuifengxiao.com'>www.yishuifengxiao.com</a>注意事项："
			+ "                            <br> 1、请不要在其他网站上使用相同的邮箱和密码进行注册。"
			+ "                            <br> 2、请不要告知任何人您的<b>yishuifengxiao</b>密码信息，包括本网站相关的工作人员。"
			+ "                            <br>" + "                            <br> 如果您错误的收到本电子邮件，请您忽略上述内容。"
			+ "                        </div>" + "                        <br>" + "                        <hr"
			+ "                            style='BORDER-BOTTOM: #808080 0px dashed; BORDER-LEFT: #808080 0px dashed; HEIGHT: 1px; BORDER-TOP: #808080 1px dashed; BORDER-RIGHT: #808080 0px dashed'>"
			+ "                        <div>" + "                            <br>" + "                        </div>"
			+ "                        <div style='TEXT-ALIGN: right; FONT-SIZE: 11pt'>"
			+ "                            yishuifengxiao" + "                        </div>"
			+ "                        <div style='TEXT-ALIGN: right; FONT-SIZE: 11pt'>"
			+ "                            {2}" + "                        </div>" + "                    </div>"
			+ "                </td>" + "            </tr>" + "        </tbody>" + "    </table>" + "" + "" + "" + ""
			+ "    <style type='text/css'>" + "        body '{'" + "font-size: 14px;"
			+ "            font-family: arial, verdana, sans-serif;" + "            line-height: 1.666;"
			+ "            padding: 0;" + "            margin: 0;" + "            overflow: auto;"
			+ "            white-space: normal;" + "            word-wrap: break-word;"
			+ "            min-height: 100px" + "        '}'" + "" + "        td," + "        input,"
			+ "        button," + "        select," + "        body '{'"
			+ "            font-family: Helvetica, 'Microsoft Yahei', verdana" + "        '}'" + "" + "        pre '{'"
			+ "            white-space: pre-wrap;" + "            white-space: -moz-pre-wrap;"
			+ "            white-space: -pre-wrap;" + "            white-space: -o-pre-wrap;"
			+ "            word-wrap: break-word;" + "            width: 95%" + "        '}'" + "" + "        th,"
			+ "        td '{'" + "            font-family: arial, verdana, sans-serif;"
			+ "            line-height: 1.666" + "        '}'" + "" + "        img '{'" + "            border: 0"
			+ "        '}'" + "" + "        header," + "        footer," + "        section," + "        aside,"
			+ "        article," + "        nav," + "        hgroup," + "        figure," + "        figcaption '{'"
			+ "            display: block" + "        '}'" + "" + "        blockquote '{'"
			+ "            margin-right: 0px" + "        '}'" + "    </style>" + "" + "" + ""
			+ "    <style id='netease_mail_footer_style' type='text/css'>" + "        #netease_mail_footer '{'"
			+ "            display: none;" + "        '}'" + "    </style>" + "" + ""
			+ "    <style id='ntes_link_color' type='text/css'>" + "        a," + "        td a '{'"
			+ "            color: #064977" + "        '}'" + "    </style>" + "" + "</body>";

}
