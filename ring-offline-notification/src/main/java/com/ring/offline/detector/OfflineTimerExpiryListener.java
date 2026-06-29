package com.ring.offline.detector;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.ring.offline.config.RingOfflineProperties;

@Component
public class OfflineTimerExpiryListener implements MessageListener {

	private final OfflineDetectionStrategy offlineDetectionStrategy;

	private final String offlineTimerKeyPrefix;

	public OfflineTimerExpiryListener(OfflineDetectionStrategy offlineDetectionStrategy,
			RingOfflineProperties properties) {
		this.offlineDetectionStrategy = offlineDetectionStrategy;
		this.offlineTimerKeyPrefix = properties.getRedis().getOfflineTimerKeyPrefix();
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();
		if (!expiredKey.startsWith(this.offlineTimerKeyPrefix)) {
			return;
		}
		String deviceId = expiredKey.substring(this.offlineTimerKeyPrefix.length());
		this.offlineDetectionStrategy.onTimerExpired(deviceId);
	}

}
