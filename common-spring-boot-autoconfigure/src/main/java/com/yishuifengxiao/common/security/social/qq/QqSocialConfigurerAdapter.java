package com.yishuifengxiao.common.security.social.qq;

import javax.sql.DataSource;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

/**
 * 存储了业务用户和服务提供商用户之间的映射关系
 * 
 * @author yishui
 * @date 2019年7月14日
 * @version 1.0.0
 */
public class QqSocialConfigurerAdapter extends SocialConfigurerAdapter {

	private DataSource dataSource;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository usersConnectionRepository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		// 设置表名前缀
		// usersConnectionRepository.setTablePrefix("dsdsf");
		return usersConnectionRepository;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public QqSocialConfigurerAdapter(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public QqSocialConfigurerAdapter() {

	}

}
