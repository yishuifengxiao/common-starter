/**
 * 
 */
package com.yishuifengxiao.common.security.remerberme;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;



/**
 * 实现记住我功能
 * @author yishui
 * @date 2018年11月23日
 * @Version 0.0.1
 */
public class CustomPersistentTokenRepository implements PersistentTokenRepository {
	Map<String,PersistentRememberMeToken> map=new HashMap<>();


	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		map.put(token.getSeries(), token);
	}


	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		PersistentRememberMeToken token = getTokenForSeries(series);

		PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(), series, tokenValue,
				new Date());
		map.put(token.getSeries(), newToken);
	}


	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		return null;
	}


	@Override
	public void removeUserTokens(String username) {
		Iterator<String> it=	map.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			if(StringUtils.equals(username, key)){
				map.keySet().remove(key);
			}
		}

	}

}
