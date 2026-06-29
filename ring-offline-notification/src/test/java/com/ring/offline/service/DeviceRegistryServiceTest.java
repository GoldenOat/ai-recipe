package com.ring.offline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ring.offline.api.DeviceNotFoundException;
import com.ring.offline.domain.DeviceEntity;
import com.ring.offline.domain.DeviceState;
import com.ring.offline.repository.DeviceRepository;

@ExtendWith(MockitoExtension.class)
class DeviceRegistryServiceTest {

	@Mock
	private DeviceRepository deviceRepository;

	@Mock
	private DeviceEntity deviceEntity;

	@InjectMocks
	private DeviceRegistryService deviceRegistryService;

	@Test
	void getActiveDeviceReturnsDeviceWhenActive() {
		given(this.deviceRepository.findByDeviceIdAndState("cam-001", DeviceState.ACTIVE))
			.willReturn(Optional.of(this.deviceEntity));

		DeviceEntity result = this.deviceRegistryService.getActiveDevice("cam-001");

		assertThat(result).isSameAs(this.deviceEntity);
		verify(this.deviceRepository).findByDeviceIdAndState("cam-001", DeviceState.ACTIVE);
	}

	@Test
	void getActiveDeviceThrowsWhenNotFound() {
		given(this.deviceRepository.findByDeviceIdAndState("missing", DeviceState.ACTIVE))
			.willReturn(Optional.empty());

		assertThatThrownBy(() -> this.deviceRegistryService.getActiveDevice("missing"))
			.isInstanceOf(DeviceNotFoundException.class)
			.hasMessageContaining("missing");
	}

}
