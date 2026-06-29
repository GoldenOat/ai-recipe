package com.ring.offline.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class DeviceEntity {

	@Id
	@Column(name = "device_id", length = 64)
	private String deviceId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(name = "device_name", nullable = false)
	private String deviceName;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false, length = 20)
	private DeviceState state;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected DeviceEntity() {
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public UserEntity getUser() {
		return this.user;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public DeviceState getState() {
		return this.state;
	}

	public Instant getCreatedAt() {
		return this.createdAt;
	}

	public Instant getUpdatedAt() {
		return this.updatedAt;
	}

}
