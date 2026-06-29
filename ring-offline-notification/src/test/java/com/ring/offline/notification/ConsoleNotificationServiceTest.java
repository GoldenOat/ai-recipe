package com.ring.offline.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DeviceState;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.domain.StatusChangeEvent;
import com.ring.offline.repository.DeviceRepository;

@ExtendWith(MockitoExtension.class)
class ConsoleNotificationServiceTest {

	@Mock
	private DeviceRepository deviceRepository;

	@Test
	void notifySkipsWhenDeviceNotActive() {
		RingOfflineProperties properties = new RingOfflineProperties();
		ConsoleNotificationService service = new ConsoleNotificationService(this.deviceRepository, properties);
		StatusChangeEvent event = new StatusChangeEvent("cam-001", DeviceStatus.ONLINE, DeviceStatus.OFFLINE,
				Instant.parse("2026-06-29T10:15:00Z"));
		given(this.deviceRepository.findActiveDeviceWithUser("cam-001", DeviceState.ACTIVE)).willReturn(Optional.empty());

		service.notify(event);

		verify(this.deviceRepository).findActiveDeviceWithUser("cam-001", DeviceState.ACTIVE);
	}

	@Test
	void notifySkipsWhenConsoleDisabled() {
		RingOfflineProperties properties = new RingOfflineProperties();
		properties.getNotification().setConsoleEnabled(false);
		ConsoleNotificationService service = new ConsoleNotificationService(this.deviceRepository, properties);
		StatusChangeEvent event = new StatusChangeEvent("cam-001", DeviceStatus.ONLINE, DeviceStatus.OFFLINE,
				Instant.parse("2026-06-29T10:15:00Z"));

		service.notify(event);

		verify(this.deviceRepository, never()).findActiveDeviceWithUser(any(), any());
	}

}
