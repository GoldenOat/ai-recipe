package com.ring.offline.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.ring.offline.config.RingOfflineProperties;

@Repository
public class RedisOfflineTimerRepository implements OfflineTimerRepository {

	private final StringRedisTemplate redisTemplate;

	private final String keyPrefix;

	private final Duration timerDuration;

	public RedisOfflineTimerRepository(StringRedisTemplate redisTemplate, RingOfflineProperties properties) {
		this.redisTemplate = redisTemplate;
		this.keyPrefix = properties.getRedis().getOfflineTimerKeyPrefix();
		this.timerDuration = properties.getOffline().getThresholdDuration();
	}

	@Override
	public void resetTimer(String deviceId) {
		this.redisTemplate.opsForValue().set(key(deviceId), "1", this.timerDuration);
	}

	String key(String deviceId) {
		return this.keyPrefix + deviceId;
	}

	String keyPrefix() {
		return this.keyPrefix;
	}

}
