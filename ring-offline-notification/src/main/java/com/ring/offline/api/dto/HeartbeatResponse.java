package com.ring.offline.api.dto;

import java.time.Instant;

public record HeartbeatResponse(String deviceId, Instant acceptedAt) {
}
