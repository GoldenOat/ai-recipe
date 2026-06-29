package com.ring.offline.service;

import java.time.Clock;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ring.offline.api.dto.DeviceStatusResponse;
import com.ring.offline.api.dto.HeartbeatResponse;
import com.ring.offline.domain.DeviceStatus;

@Service
public class HeartbeatService {

	private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);

	private final DeviceRegistryService deviceRegistryService;

	private final Clock clock;

	public HeartbeatService(DeviceRegistryService deviceRegistryService, Clock clock) {
		this.deviceRegistryService = deviceRegistryService;
		this.clock = clock;
	}

	public HeartbeatResponse acceptHeartbeat(String deviceId, Instant timestamp) {
		this.deviceRegistryService.getActiveDevice(deviceId);
		Instant heartbeatTime = timestamp != null ? timestamp : this.clock.instant();
		Instant acceptedAt = this.clock.instant();
		log.info("Heartbeat accepted for device {} at {}", deviceId, heartbeatTime);
		return new HeartbeatResponse(deviceId, acceptedAt);
	}

	public DeviceStatusResponse getStatus(String deviceId) {
		this.deviceRegistryService.getActiveDevice(deviceId);
		return new DeviceStatusResponse(deviceId, DeviceStatus.ONLINE, null);
	}

}
