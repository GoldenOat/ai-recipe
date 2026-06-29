package com.ring.offline.detector;

public interface OfflineDetectionStrategy {

	void onTimerExpired(String deviceId);

}
