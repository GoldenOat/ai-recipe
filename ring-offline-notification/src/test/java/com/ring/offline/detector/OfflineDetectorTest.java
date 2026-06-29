package com.ring.offline.detector;

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

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DevicePresence;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.StatusChangeEvent;
import com.ring.offline.messaging.StatusChangePublisher;
import com.ring.offline.repository.PresenceRepository;

@ExtendWith(MockitoExtension.class)
class OfflineDetectorTest {

	private static final Instant NOW = Instant.parse("2026-06-29T10:15:00Z");

	@Mock
	private PresenceRepository presenceRepository;

	@Mock
	private StatusChangePublisher statusChangePublisher;

	private OfflineDetector offlineDetector;

	@BeforeEach
	void setUp() {
		RingOfflineProperties properties = new RingOfflineProperties();
		properties.getOffline().setThresholdMinutes(10);
		Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
		this.offlineDetector = new OfflineDetector(this.presenceRepository, this.statusChangePublisher, properties,
				clock);
	}

	@Test
	void markOfflineIfStaleTransitionsOnlineDevice() {
		DevicePresence presence = new DevicePresence("cam-001", DeviceStatus.ONLINE,
				Instant.parse("2026-06-29T10:00:00Z"), Instant.parse("2026-06-29T10:00:00Z"));
		given(this.presenceRepository.findById("cam-001")).willReturn(Optional.of(presence));

		this.offlineDetector.onTimerExpired("cam-001");

		ArgumentCaptor<DevicePresence> presenceCaptor = ArgumentCaptor.forClass(DevicePresence.class);
		verify(this.presenceRepository).save(presenceCaptor.capture());
		assertThat(presenceCaptor.getValue().status()).isEqualTo(DeviceStatus.OFFLINE);

		ArgumentCaptor<StatusChangeEvent> eventCaptor = ArgumentCaptor.forClass(StatusChangeEvent.class);
		verify(this.statusChangePublisher).publish(eventCaptor.capture());
		assertThat(eventCaptor.getValue().newStatus()).isEqualTo(DeviceStatus.OFFLINE);
	}

	@Test
	void markOfflineIfStaleSkipsWhenHeartbeatIsRecent() {
		DevicePresence presence = new DevicePresence("cam-001", DeviceStatus.ONLINE,
				Instant.parse("2026-06-29T10:10:00Z"), Instant.parse("2026-06-29T10:10:00Z"));
		given(this.presenceRepository.findById("cam-001")).willReturn(Optional.of(presence));

		this.offlineDetector.onTimerExpired("cam-001");

		verify(this.presenceRepository, never()).save(any());
		verify(this.statusChangePublisher, never()).publish(any());
	}

}
