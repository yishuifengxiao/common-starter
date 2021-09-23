/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yishuifengxiao.common.security.remerberme;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * <p>Simple <tt>PersistentTokenRepository</tt> implementation backed by a Map.
 * Intended for testing only. </p>
 * <p>
 * 解决原生类报错DataIntegrityViolationException再未引入数据库相关的包时出错的问题
 * </p>
 * 
 * @author Luke Taylor *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class InMemoryTokenRepositoryImpl implements PersistentTokenRepository {
	private final Map<String, PersistentRememberMeToken> seriesTokens = new HashMap<>();

	@Override
	public synchronized void createNewToken(PersistentRememberMeToken token) {
		PersistentRememberMeToken current = seriesTokens.get(token.getSeries());

		if (current != null) {
			throw new RuntimeException("Series Id '" + token.getSeries() + "' already exists!");
		}

		seriesTokens.put(token.getSeries(), token);
	}

	@Override
	public synchronized void updateToken(String series, String tokenValue, Date lastUsed) {
		PersistentRememberMeToken token = getTokenForSeries(series);

		PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(), series, tokenValue,
				new Date());

		// Store it, overwriting the existing one.
		seriesTokens.put(series, newToken);
	}

	@Override
	public synchronized PersistentRememberMeToken getTokenForSeries(String seriesId) {
		return seriesTokens.get(seriesId);
	}

	@Override
	public synchronized void removeUserTokens(String username) {
		Iterator<String> series = seriesTokens.keySet().iterator();

		while (series.hasNext()) {
			String seriesId = series.next();

			PersistentRememberMeToken token = seriesTokens.get(seriesId);

			if (username.equals(token.getUsername())) {
				series.remove();
			}
		}
	}
}
