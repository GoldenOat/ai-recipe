package com.ring.offline.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ring.offline.api.dto.DeviceStatusResponse;
import com.ring.offline.api.dto.HeartbeatRequest;
import com.ring.offline.api.dto.HeartbeatResponse;
import com.ring.offline.service.HeartbeatService;

@RestController
@RequestMapping("/api/v1/devices")
public class HeartbeatController {

	private final HeartbeatService heartbeatService;

	public HeartbeatController(HeartbeatService heartbeatService) {
		this.heartbeatService = heartbeatService;
	}

	@PostMapping("/{deviceId}/heartbeats")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public HeartbeatResponse heartbeat(@PathVariable String deviceId,
			@RequestBody(required = false) HeartbeatRequest request) {
		var timestamp = request != null ? request.timestamp() : null;
		return this.heartbeatService.acceptHeartbeat(deviceId, timestamp);
	}

	@GetMapping("/{deviceId}/status")
	public DeviceStatusResponse status(@PathVariable String deviceId) {
		return this.heartbeatService.getStatus(deviceId);
	}

}
