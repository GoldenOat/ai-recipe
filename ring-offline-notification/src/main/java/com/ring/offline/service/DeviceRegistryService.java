package com.ring.offline.service;

import org.springframework.stereotype.Service;

import com.ring.offline.api.DeviceNotFoundException;
import com.ring.offline.domain.DeviceEntity;
import com.ring.offline.domain.DeviceState;
import com.ring.offline.repository.DeviceRepository;

@Service
public class DeviceRegistryService {

	private final DeviceRepository deviceRepository;

	public DeviceRegistryService(DeviceRepository deviceRepository) {
		this.deviceRepository = deviceRepository;
	}

	public DeviceEntity getActiveDevice(String deviceId) {
		return this.deviceRepository.findByDeviceIdAndState(deviceId, DeviceState.ACTIVE)
			.orElseThrow(() -> new DeviceNotFoundException(deviceId));
	}

}
