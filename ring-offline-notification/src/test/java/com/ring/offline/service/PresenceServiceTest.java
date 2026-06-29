package com.ring.offline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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

import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.HeartbeatEvent;
import com.ring.offline.domain.StatusChangeEvent;
import com.ring.offline.messaging.StatusChangePublisher;
import com.ring.offline.repository.OfflineTimerRepository;
import com.ring.offline.repository.PresenceRepository;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

	private static final Instant NOW = Instant.parse("2026-06-29T10:00:00Z");

	@Mock
	private PresenceRepository presenceRepository;

	@Mock
	private OfflineTimerRepository offlineTimerRepository;

	@Mock
	private StatusChangePublisher statusChangePublisher;

	private PresenceService presenceService;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
		this.presenceService = new PresenceService(this.presenceRepository, this.offlineTimerRepository,
				this.statusChangePublisher, clock);
	}

	@Test
	void onHeartbeatSavesOnlinePresenceAndResetsTimer() {
		HeartbeatEvent event = new HeartbeatEvent("cam-001", NOW.minusSeconds(30), NOW);
		given(this.presenceRepository.findById("cam-001")).willReturn(Optional.empty());

		this.presenceService.onHeartbeat(event);

		ArgumentCaptor<DevicePresence> captor = ArgumentCaptor.forClass(DevicePresence.class);
		verify(this.presenceRepository).save(captor.capture());
		assertThat(captor.getValue().status()).isEqualTo(DeviceStatus.ONLINE);
		assertThat(captor.getValue().lastSeen()).isEqualTo(NOW.minusSeconds(30));
		verify(this.offlineTimerRepository).resetTimer("cam-001");
		verify(this.statusChangePublisher, never()).publish(any());
	}

	@Test
	void onHeartbeatPublishesStatusChangeWhenRecoveringFromOffline() {
		HeartbeatEvent event = new HeartbeatEvent("cam-001", NOW.minusSeconds(30), NOW);
		DevicePresence offline = new DevicePresence("cam-001", DeviceStatus.OFFLINE, NOW.minusSeconds(600), NOW);
		given(this.presenceRepository.findById("cam-001")).willReturn(Optional.of(offline));

		this.presenceService.onHeartbeat(event);

		ArgumentCaptor<StatusChangeEvent> captor = ArgumentCaptor.forClass(StatusChangeEvent.class);
		verify(this.statusChangePublisher).publish(captor.capture());
		assertThat(captor.getValue().previousStatus()).isEqualTo(DeviceStatus.OFFLINE);
		assertThat(captor.getValue().newStatus()).isEqualTo(DeviceStatus.ONLINE);
	}

}
