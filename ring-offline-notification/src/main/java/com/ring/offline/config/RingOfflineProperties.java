package com.ring.offline.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ring")
public class RingOfflineProperties {

	private final Offline offline = new Offline();

	private final Kafka kafka = new Kafka();

	private final Notification notification = new Notification();

	private final Redis redis = new Redis();

	public Offline getOffline() {
		return this.offline;
	}

	public Kafka getKafka() {
		return this.kafka;
	}

	public Notification getNotification() {
		return this.notification;
	}

	public Redis getRedis() {
		return this.redis;
	}

	public static class Offline {

		private int thresholdMinutes = 10;

		public int getThresholdMinutes() {
			return this.thresholdMinutes;
		}

		public void setThresholdMinutes(int thresholdMinutes) {
			this.thresholdMinutes = thresholdMinutes;
		}

	}

	public static class Kafka {

		private boolean mockEnabled = true;

		private final Topics topics = new Topics();

		public boolean isMockEnabled() {
			return this.mockEnabled;
		}

		public void setMockEnabled(boolean mockEnabled) {
			this.mockEnabled = mockEnabled;
		}

		public Topics getTopics() {
			return this.topics;
		}

		public static class Topics {

			private String heartbeats = "device.heartbeats";

			private String statusChanges = "device.status-changes";

			public String getHeartbeats() {
				return this.heartbeats;
			}

			public void setHeartbeats(String heartbeats) {
				this.heartbeats = heartbeats;
			}

			public String getStatusChanges() {
				return this.statusChanges;
			}

			public void setStatusChanges(String statusChanges) {
				this.statusChanges = statusChanges;
			}

		}

	}

	public static class Notification {

		private boolean consoleEnabled = true;

		public boolean isConsoleEnabled() {
			return this.consoleEnabled;
		}

		public void setConsoleEnabled(boolean consoleEnabled) {
			this.consoleEnabled = consoleEnabled;
		}

	}

	public static class Redis {

		private String presenceKeyPrefix = "presence:";

		private String offlineTimerKeyPrefix = "offline-timer:";

		public String getPresenceKeyPrefix() {
			return this.presenceKeyPrefix;
		}

		public void setPresenceKeyPrefix(String presenceKeyPrefix) {
			this.presenceKeyPrefix = presenceKeyPrefix;
		}

		public String getOfflineTimerKeyPrefix() {
			return this.offlineTimerKeyPrefix;
		}

		public void setOfflineTimerKeyPrefix(String offlineTimerKeyPrefix) {
			this.offlineTimerKeyPrefix = offlineTimerKeyPrefix;
		}

	}

}
