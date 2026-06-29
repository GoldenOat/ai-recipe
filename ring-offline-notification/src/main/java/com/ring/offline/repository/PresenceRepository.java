package com.ring.offline.repository;

import java.util.Optional;

import com.ring.offline.domain.DevicePresence;

public interface PresenceRepository {

	Optional<DevicePresence> findById(String deviceId);

	void save(DevicePresence presence);

}
