package com.ring.offline.domain;

import java.time.Instant;

public record DevicePresence(String deviceId, DeviceStatus status, Instant lastSeen, Instant updatedAt) {
}
