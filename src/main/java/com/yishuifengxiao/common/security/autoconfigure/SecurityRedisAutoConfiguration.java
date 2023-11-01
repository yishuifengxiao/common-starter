package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.redis.RedisCoreAutoConfiguration;
import com.yishuifengxiao.common.security.httpsecurity.authorize.rememberme.RedisTokenRepository;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.security.token.holder.impl.RedisTokenHolder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * 配置基于Redis的记住密码策略配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(value = {RedisCoreAutoConfiguration.class})
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class, RedisOperations.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
public class SecurityRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PersistentTokenRepository.class)
    public PersistentTokenRepository redisTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        RedisTokenRepository redisTokenRepository = new RedisTokenRepository(redisTemplate);
        return redisTokenRepository;
    }


    @Bean
    @ConditionalOnMissingBean({TokenHolder.class})
    public TokenHolder tokenHolder(RedisTemplate<String, Object> redisTemplate) {
        RedisTokenHolder redisTokenHolder = new RedisTokenHolder();
        redisTokenHolder.setRedisTemplate(redisTemplate);
        return redisTokenHolder;
    }
}
