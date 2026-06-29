package com.ring.offline.repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;

@Repository
public class RedisPresenceRepository implements PresenceRepository {

	private final StringRedisTemplate redisTemplate;

	private final String keyPrefix;

	public RedisPresenceRepository(StringRedisTemplate redisTemplate, RingOfflineProperties properties) {
		this.redisTemplate = redisTemplate;
		this.keyPrefix = properties.getRedis().getPresenceKeyPrefix();
	}

	@Override
	public Optional<DevicePresence> findById(String deviceId) {
		Map<Object, Object> entries = this.redisTemplate.opsForHash().entries(key(deviceId));
		if (entries.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(toPresence(deviceId, entries));
	}

	@Override
	public void save(DevicePresence presence) {
		String key = key(presence.deviceId());
		this.redisTemplate.opsForHash().put(key, "status", presence.status().name());
		this.redisTemplate.opsForHash().put(key, "lastSeen", presence.lastSeen().toString());
		this.redisTemplate.opsForHash().put(key, "updatedAt", presence.updatedAt().toString());
	}

	private String key(String deviceId) {
		return this.keyPrefix + deviceId;
	}

	private static DevicePresence toPresence(String deviceId, Map<Object, Object> entries) {
		DeviceStatus status = DeviceStatus.valueOf((String) entries.get("status"));
		Instant lastSeen = Instant.parse((String) entries.get("lastSeen"));
		Instant updatedAt = Instant.parse((String) entries.get("updatedAt"));
		return new DevicePresence(deviceId, status, lastSeen, updatedAt);
	}

}
