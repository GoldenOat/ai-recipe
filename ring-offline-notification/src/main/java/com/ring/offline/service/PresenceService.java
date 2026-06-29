package com.ring.offline.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.HeartbeatEvent;
import com.ring.offline.domain.StatusChangeEvent;
import com.ring.offline.messaging.StatusChangePublisher;
import com.ring.offline.repository.OfflineTimerRepository;
import com.ring.offline.repository.PresenceRepository;

@Service
public class PresenceService implements com.ring.offline.messaging.HeartbeatEventHandler {

	private final PresenceRepository presenceRepository;

	private final OfflineTimerRepository offlineTimerRepository;

	private final StatusChangePublisher statusChangePublisher;

	private final Clock clock;

	public PresenceService(PresenceRepository presenceRepository, OfflineTimerRepository offlineTimerRepository,
			StatusChangePublisher statusChangePublisher, Clock clock) {
		this.presenceRepository = presenceRepository;
		this.offlineTimerRepository = offlineTimerRepository;
		this.statusChangePublisher = statusChangePublisher;
		this.clock = clock;
	}

	@Override
	public void onHeartbeat(HeartbeatEvent event) {
		DevicePresence current = this.presenceRepository.findById(event.deviceId())
			.orElse(new DevicePresence(event.deviceId(), DeviceStatus.ONLINE, event.timestamp(), event.receivedAt()));
		Instant updatedAt = this.clock.instant();
		DeviceStatus previousStatus = current.status();

		DevicePresence updated = new DevicePresence(event.deviceId(), DeviceStatus.ONLINE, event.timestamp(),
				updatedAt);
		this.presenceRepository.save(updated);
		this.offlineTimerRepository.resetTimer(event.deviceId());

		if (previousStatus == DeviceStatus.OFFLINE) {
			this.statusChangePublisher.publish(new StatusChangeEvent(event.deviceId(), DeviceStatus.OFFLINE,
					DeviceStatus.ONLINE, updatedAt));
		}
	}

	public Optional<DevicePresence> getPresence(String deviceId) {
		return this.presenceRepository.findById(deviceId);
	}

}
