package com.ring.offline.api;

public class DeviceNotFoundException extends RuntimeException {

	private final String deviceId;

	public DeviceNotFoundException(String deviceId) {
		super("Device not found or not active: " + deviceId);
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

}
