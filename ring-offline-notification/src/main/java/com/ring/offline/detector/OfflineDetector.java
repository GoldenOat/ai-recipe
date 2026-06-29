package com.ring.offline.detector;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.StatusChangeEvent;
import com.ring.offline.messaging.StatusChangePublisher;
import com.ring.offline.repository.PresenceRepository;

@Service
public class OfflineDetector implements OfflineDetectionStrategy {

	private static final Logger log = LoggerFactory.getLogger(OfflineDetector.class);

	private final PresenceRepository presenceRepository;

	private final StatusChangePublisher statusChangePublisher;

	private final RingOfflineProperties properties;

	private final Clock clock;

	public OfflineDetector(PresenceRepository presenceRepository, StatusChangePublisher statusChangePublisher,
			RingOfflineProperties properties, Clock clock) {
		this.presenceRepository = presenceRepository;
		this.statusChangePublisher = statusChangePublisher;
		this.properties = properties;
		this.clock = clock;
	}

	@Override
	public void onTimerExpired(String deviceId) {
		this.presenceRepository.findById(deviceId).ifPresent(this::markOfflineIfStale);
	}

	void markOfflineIfStale(DevicePresence presence) {
		Duration threshold = this.properties.getOffline().getThresholdDuration();
		Instant cutoff = this.clock.instant().minus(threshold);
		if (presence.lastSeen().isAfter(cutoff)) {
			return;
		}
		if (presence.status() != DeviceStatus.ONLINE) {
			return;
		}
		Instant changedAt = this.clock.instant();
		DevicePresence offline = new DevicePresence(presence.deviceId(), DeviceStatus.OFFLINE, presence.lastSeen(),
				changedAt);
		this.presenceRepository.save(offline);
		log.info("[OFFLINE-DETECTOR] device={} lastSeen={} threshold={}s -> OFFLINE", presence.deviceId(),
				presence.lastSeen(), this.properties.getOffline().getThresholdSeconds());
		this.statusChangePublisher.publish(new StatusChangeEvent(presence.deviceId(), DeviceStatus.ONLINE,
				DeviceStatus.OFFLINE, changedAt));
	}

}
