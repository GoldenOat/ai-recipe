package com.ring.offline.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.DeviceState;
import com.ring.offline.domain.StatusChangeEvent;
import com.ring.offline.repository.DeviceRepository;

@Service
public class ConsoleNotificationService implements NotificationService, com.ring.offline.messaging.StatusChangeEventHandler {

	private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationService.class);

	private final DeviceRepository deviceRepository;

	private final RingOfflineProperties properties;

	public ConsoleNotificationService(DeviceRepository deviceRepository, RingOfflineProperties properties) {
		this.deviceRepository = deviceRepository;
		this.properties = properties;
	}

	@Override
	public void onStatusChange(StatusChangeEvent event) {
		notify(event);
	}

	@Override
	public void notify(StatusChangeEvent event) {
		if (!this.properties.getNotification().isConsoleEnabled()) {
			return;
		}
		this.deviceRepository.findActiveDeviceWithUser(event.deviceId(), DeviceState.ACTIVE).ifPresent(device -> {
			String userId = device.getUser().getUserId();
			log.info("[NOTIFICATION] user={} device={} name={} status={} at={}", userId, event.deviceId(),
					device.getDeviceName(), event.newStatus(), event.changedAt());
		});
	}

}
