package com.ring.offline.detector;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.repository.PresenceRepository;

@Component
public class ScheduledOfflineSweep {

	private final PresenceRepository presenceRepository;

	private final OfflineDetector offlineDetector;

	private final StringRedisTemplate redisTemplate;

	private final String presenceKeyPrefix;

	private final RingOfflineProperties properties;

	private final Clock clock;

	public ScheduledOfflineSweep(PresenceRepository presenceRepository, OfflineDetector offlineDetector,
			StringRedisTemplate redisTemplate, RingOfflineProperties properties, Clock clock) {
		this.presenceRepository = presenceRepository;
		this.offlineDetector = offlineDetector;
		this.redisTemplate = redisTemplate;
		this.presenceKeyPrefix = properties.getRedis().getPresenceKeyPrefix();
		this.properties = properties;
		this.clock = clock;
	}

	@Scheduled(fixedDelayString = "${ring.offline.sweep-interval-ms:60000}")
	void sweepStaleOnlineDevices() {
		Instant cutoff = this.clock.instant()
			.minus(Duration.ofMinutes(this.properties.getOffline().getThresholdMinutes()));
		for (String deviceId : findOnlineDeviceIds()) {
			this.presenceRepository.findById(deviceId)
				.filter(presence -> presence.status() == DeviceStatus.ONLINE)
				.filter(presence -> !presence.lastSeen().isAfter(cutoff))
				.ifPresent(this.offlineDetector::markOfflineIfStale);
		}
	}

	private List<String> findOnlineDeviceIds() {
		List<String> deviceIds = new ArrayList<>();
		ScanOptions options = ScanOptions.scanOptions().match(this.presenceKeyPrefix + "*").count(100).build();
		try (Cursor<String> cursor = this.redisTemplate.scan(options)) {
			while (cursor.hasNext()) {
				String key = cursor.next();
				deviceIds.add(key.substring(this.presenceKeyPrefix.length()));
			}
		}
		return deviceIds;
	}

}
