package com.ring.offline.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.HeartbeatEvent;
import com.ring.offline.domain.StatusChangeEvent;

@Service
@ConditionalOnProperty(prefix = "ring.kafka", name = "mock-enabled", havingValue = "true", matchIfMissing = true)
public class LoggingEventPublisher implements EventPublisher {

	private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

	private final RingOfflineProperties properties;

	private final HeartbeatEventHandler heartbeatEventHandler;

	private final StatusChangeEventHandler statusChangeEventHandler;

	private final ObjectMapper objectMapper;

	public LoggingEventPublisher(RingOfflineProperties properties, @Lazy HeartbeatEventHandler heartbeatEventHandler,
			StatusChangeEventHandler statusChangeEventHandler, ObjectMapper objectMapper) {
		this.properties = properties;
		this.heartbeatEventHandler = heartbeatEventHandler;
		this.statusChangeEventHandler = statusChangeEventHandler;
		this.objectMapper = objectMapper;
	}

	@Override
	public void publish(String topic, Object event) {
		String partitionKey = partitionKey(event);
		String payload = serialize(event);
		log.info("[KAFKA-PUBLISH] topic={} partition=key:{} payload={}", topic, partitionKey, payload);
		log.info("[KAFKA-CONSUME] topic={} partition=key:{} payload={}", topic, partitionKey, payload);
		dispatch(topic, event);
	}

	private void dispatch(String topic, Object event) {
		if (topic.equals(this.properties.getKafka().getTopics().getHeartbeats()) && event instanceof HeartbeatEvent heartbeatEvent) {
			this.heartbeatEventHandler.onHeartbeat(heartbeatEvent);
			return;
		}
		if (topic.equals(this.properties.getKafka().getTopics().getStatusChanges())
				&& event instanceof StatusChangeEvent statusChangeEvent) {
			this.statusChangeEventHandler.onStatusChange(statusChangeEvent);
		}
	}

	private String partitionKey(Object event) {
		if (event instanceof HeartbeatEvent heartbeatEvent) {
			return heartbeatEvent.deviceId();
		}
		if (event instanceof StatusChangeEvent statusChangeEvent) {
			return statusChangeEvent.deviceId();
		}
		return "unknown";
	}

	private String serialize(Object event) {
		try {
			return this.objectMapper.writeValueAsString(event);
		}
		catch (JsonProcessingException exception) {
			return event.toString();
		}
	}

}
