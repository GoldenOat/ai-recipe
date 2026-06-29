package com.ring.offline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ring.offline.api.DeviceNotFoundException;
import com.ring.offline.api.dto.DeviceStatusResponse;
import com.ring.offline.api.dto.HeartbeatResponse;
import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DeviceEntity;
import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.HeartbeatEvent;
import com.ring.offline.messaging.EventPublisher;

@ExtendWith(MockitoExtension.class)
class HeartbeatServiceTest {

	private static final Instant FIXED_NOW = Instant.parse("2026-06-29T10:00:00Z");

	@Mock
	private DeviceRegistryService deviceRegistryService;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private PresenceService presenceService;

	@Mock
	private DeviceEntity deviceEntity;

	private Clock clock;

	private RingOfflineProperties properties;

	private HeartbeatService heartbeatService;

	@BeforeEach
	void setUp() {
		this.clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
		this.properties = new RingOfflineProperties();
		this.heartbeatService = new HeartbeatService(this.deviceRegistryService, this.eventPublisher,
				this.presenceService, this.properties, this.clock);
	}

	@Test
	void acceptHeartbeatPublishesEvent() {
		Instant heartbeatTime = Instant.parse("2026-06-29T09:59:00Z");
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);

		HeartbeatResponse response = this.heartbeatService.acceptHeartbeat("cam-001", heartbeatTime);

		assertThat(response.deviceId()).isEqualTo("cam-001");
		assertThat(response.acceptedAt()).isEqualTo(FIXED_NOW);
		ArgumentCaptor<HeartbeatEvent> captor = ArgumentCaptor.forClass(HeartbeatEvent.class);
		verify(this.eventPublisher).publish(eq(this.properties.getKafka().getTopics().getHeartbeats()),
				captor.capture());
		assertThat(captor.getValue().deviceId()).isEqualTo("cam-001");
		assertThat(captor.getValue().timestamp()).isEqualTo(heartbeatTime);
	}

	@Test
	void acceptHeartbeatUsesClockWhenTimestampMissing() {
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);

		this.heartbeatService.acceptHeartbeat("cam-001", null);

		ArgumentCaptor<HeartbeatEvent> captor = ArgumentCaptor.forClass(HeartbeatEvent.class);
		verify(this.eventPublisher).publish(eq(this.properties.getKafka().getTopics().getHeartbeats()),
				captor.capture());
		assertThat(captor.getValue().timestamp()).isEqualTo(FIXED_NOW);
	}

	@Test
	void acceptHeartbeatPropagatesDeviceNotFound() {
		given(this.deviceRegistryService.getActiveDevice("missing")).willThrow(new DeviceNotFoundException("missing"));

		assertThatThrownBy(() -> this.heartbeatService.acceptHeartbeat("missing", FIXED_NOW))
			.isInstanceOf(DeviceNotFoundException.class);
	}

	@Test
	void getStatusReturnsPresenceWhenAvailable() {
		DevicePresence presence = new DevicePresence("cam-001", DeviceStatus.OFFLINE, FIXED_NOW, FIXED_NOW);
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);
		given(this.presenceService.getPresence("cam-001")).willReturn(Optional.of(presence));

		DeviceStatusResponse response = this.heartbeatService.getStatus("cam-001");

		assertThat(response.status()).isEqualTo(DeviceStatus.OFFLINE);
		assertThat(response.lastSeen()).isEqualTo(FIXED_NOW);
	}

	@Test
	void getStatusReturnsOnlineStubWhenNoPresence() {
		given(this.deviceRegistryService.getActiveDevice("cam-001")).willReturn(this.deviceEntity);
		given(this.presenceService.getPresence("cam-001")).willReturn(Optional.empty());

		DeviceStatusResponse response = this.heartbeatService.getStatus("cam-001");

		assertThat(response.status()).isEqualTo(DeviceStatus.ONLINE);
		assertThat(response.lastSeen()).isNull();
	}

}
