package com.ring.offline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.ring.offline.detector.OfflineTimerExpiryListener;

@Configuration
public class RedisListenerConfig {

	@Bean
	RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
			OfflineTimerExpiryListener offlineTimerExpiryListener) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(offlineTimerExpiryListener, new PatternTopic("__keyevent@*__:expired"));
		return container;
	}

}
