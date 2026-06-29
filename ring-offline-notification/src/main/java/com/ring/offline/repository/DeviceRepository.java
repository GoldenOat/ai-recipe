package com.ring.offline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ring.offline.domain.DeviceEntity;
import com.ring.offline.domain.DeviceState;

public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {

	Optional<DeviceEntity> findByDeviceIdAndState(String deviceId, DeviceState state);

}
