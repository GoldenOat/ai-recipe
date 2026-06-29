package com.ring.offline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ring.offline.api.DeviceNotFoundException;
import com.ring.offline.api.dto.DeviceStatusResponse;
import com.ring.offline.api.dto.HeartbeatResponse;
import com.ring.offline.domain.DeviceEntity;
import com.ring.offline.domain.DeviceStatus;

@ExtendWith(MockitoExtension.class)
class HeartbeatServiceTest {

	private static final Instant FIXED_NOW = Instant.parse("2026-06-29T10:00:00Z");

	@Mock
	private DeviceRegistryService deviceRegistryService;

	@Mock
	private DeviceEntity deviceEntity;

	private Clock clock;

	private HeartbeatService heartbeatService;

	@BeforeEach
	void setUp() {
		this.clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
		this.heartbeatService = new HeartbeatService(this.deviceRegistryService, this.clock);
	}

	@Test
	void acceptHeartbeatUsesProvidedTimestamp() {
		Instant heartbeatTime = Instant.parse("2026-06-29T09:59:00Z");
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);

		HeartbeatResponse response = this.heartbeatService.acceptHeartbeat("cam-001", heartbeatTime);

		assertThat(response.deviceId()).isEqualTo("cam-001");
		assertThat(response.acceptedAt()).isEqualTo(FIXED_NOW);
		verify(this.deviceRegistryService).getActiveDevice("cam-001");
	}

	@Test
	void acceptHeartbeatUsesClockWhenTimestampMissing() {
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);

		HeartbeatResponse response = this.heartbeatService.acceptHeartbeat("cam-001", null);

		assertThat(response.deviceId()).isEqualTo("cam-001");
		assertThat(response.acceptedAt()).isEqualTo(FIXED_NOW);
	}

	@Test
	void acceptHeartbeatPropagatesDeviceNotFound() {
		given(this.deviceRegistryService.getActiveDevice("missing")).willThrow(new DeviceNotFoundException("missing"));

		assertThatThrownBy(() -> this.heartbeatService.acceptHeartbeat("missing", FIXED_NOW))
			.isInstanceOf(DeviceNotFoundException.class);
	}

	@Test
	void getStatusReturnsOnlineStub() {
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);

		DeviceStatusResponse response = this.heartbeatService.getStatus("cam-001");

		assertThat(response.deviceId()).isEqualTo("cam-001");
		assertThat(response.status()).isEqualTo(DeviceStatus.ONLINE);
		assertThat(response.lastSeen()).isNull();
	}

}
