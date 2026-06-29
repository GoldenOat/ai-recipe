package com.ring.offline.domain;

import java.time.Instant;

public record StatusChangeEvent(String deviceId, DeviceStatus previousStatus, DeviceStatus newStatus,
		Instant changedAt) {
}
