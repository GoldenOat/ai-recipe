package com.ring.offline.api.dto;

import java.time.Instant;

public record HeartbeatRequest(Instant timestamp) {
}
