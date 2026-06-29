package com.ring.offline.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.ring.offline.api.dto.DeviceStatusResponse;
import com.ring.offline.api.dto.HeartbeatResponse;
import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.HeartbeatEvent;
import com.ring.offline.messaging.EventPublisher;

@Service
public class HeartbeatService {

	private final DeviceRegistryService deviceRegistryService;

	private final EventPublisher eventPublisher;

	private final PresenceService presenceService;

	private final RingOfflineProperties properties;

	private final Clock clock;

	public HeartbeatService(DeviceRegistryService deviceRegistryService, EventPublisher eventPublisher,
			PresenceService presenceService, RingOfflineProperties properties, Clock clock) {
		this.deviceRegistryService = deviceRegistryService;
		this.eventPublisher = eventPublisher;
		this.presenceService = presenceService;
		this.properties = properties;
		this.clock = clock;
	}

	public HeartbeatResponse acceptHeartbeat(String deviceId, Instant timestamp) {
		this.deviceRegistryService.getActiveDevice(deviceId);
		Instant heartbeatTime = timestamp != null ? timestamp : this.clock.instant();
		Instant acceptedAt = this.clock.instant();
		HeartbeatEvent event = new HeartbeatEvent(deviceId, heartbeatTime, acceptedAt);
		this.eventPublisher.publish(this.properties.getKafka().getTopics().getHeartbeats(), event);
		return new HeartbeatResponse(deviceId, acceptedAt);
	}

	public DeviceStatusResponse getStatus(String deviceId) {
		this.deviceRegistryService.getActiveDevice(deviceId);
		return this.presenceService.getPresence(deviceId)
			.map(presence -> new DeviceStatusResponse(deviceId, presence.status(), presence.lastSeen()))
			.orElseGet(() -> new DeviceStatusResponse(deviceId, DeviceStatus.ONLINE, null));
	}

}
