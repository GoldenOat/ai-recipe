package com.ring.offline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.ring.offline.api.dto.DeviceStatusResponse;
import com.ring.offline.api.dto.HeartbeatRequest;
import com.ring.offline.api.dto.HeartbeatResponse;
import com.ring.offline.domain.DeviceStatus;
import com.ring.offline.notification.ConsoleNotificationService;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import redis.embedded.RedisServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
class OfflineNotificationE2ETest {

	private static RedisServer embeddedRedis;

	@Autowired
	private TestRestTemplate restTemplate;

	private ListAppender<ILoggingEvent> notificationLog;

	@BeforeAll
	static void startEmbeddedRedis() throws IOException {
		embeddedRedis = RedisServer.newRedisServer().port(6370).build();
		embeddedRedis.start();
	}

	@AfterAll
	static void stopEmbeddedRedis() throws IOException {
		if (embeddedRedis != null) {
			embeddedRedis.stop();
		}
	}

	@BeforeEach
	void setUp() {
		Logger logger = (Logger) LoggerFactory.getLogger(ConsoleNotificationService.class);
		this.notificationLog = new ListAppender<>();
		this.notificationLog.start();
		logger.addAppender(this.notificationLog);
	}

	@Test
	void deviceGoesOfflineAfterThresholdAndLogsNotification() {
		Instant heartbeatTime = Instant.parse("2026-06-29T10:00:00Z");

		ResponseEntity<HeartbeatResponse> heartbeatResponse = this.restTemplate.postForEntity(
				"/api/v1/devices/cam-001/heartbeats", new HeartbeatRequest(heartbeatTime), HeartbeatResponse.class);
		assertThat(heartbeatResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(heartbeatResponse.getBody().deviceId()).isEqualTo("cam-001");

		DeviceStatusResponse onlineStatus = this.restTemplate.getForObject("/api/v1/devices/cam-001/status",
				DeviceStatusResponse.class);
		assertThat(onlineStatus.status()).isEqualTo(DeviceStatus.ONLINE);
		assertThat(onlineStatus.lastSeen()).isEqualTo(heartbeatTime);

		await().atMost(Duration.ofSeconds(15)).pollInterval(Duration.ofMillis(500)).untilAsserted(() -> {
			DeviceStatusResponse status = this.restTemplate.getForObject("/api/v1/devices/cam-001/status",
					DeviceStatusResponse.class);
			assertThat(status.status()).isEqualTo(DeviceStatus.OFFLINE);
		});

		List<String> messages = this.notificationLog.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
		assertThat(messages).anyMatch(message -> message.contains("[NOTIFICATION]"))
			.anyMatch(message -> message.contains("device=cam-001"))
			.anyMatch(message -> message.contains("status=OFFLINE"));
	}

}
