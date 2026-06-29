package com.ring.offline.api.dto;

import java.time.Instant;

import com.ring.offline.domain.DeviceStatus;

public record DeviceStatusResponse(String deviceId, DeviceStatus status, Instant lastSeen) {
}
