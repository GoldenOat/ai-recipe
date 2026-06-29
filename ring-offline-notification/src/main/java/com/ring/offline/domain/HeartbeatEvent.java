package com.ring.offline.domain;

import java.time.Instant;

public record HeartbeatEvent(String deviceId, Instant timestamp, Instant receivedAt) {
}
