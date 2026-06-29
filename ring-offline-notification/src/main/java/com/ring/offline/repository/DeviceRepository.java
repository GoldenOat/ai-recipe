package com.ring.offline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ring.offline.domain.DeviceEntity;
import com.ring.offline.domain.DeviceState;

public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {

	Optional<DeviceEntity> findByDeviceIdAndState(String deviceId, DeviceState state);

	@Query("SELECT d FROM DeviceEntity d JOIN FETCH d.user WHERE d.deviceId = :deviceId AND d.state = :state")
	Optional<DeviceEntity> findActiveDeviceWithUser(@Param("deviceId") String deviceId,
			@Param("state") DeviceState state);

}
