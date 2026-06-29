package com.ring.offline.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ring.offline.config.RingOfflineProperties;
import com.ring.offline.domain.StatusChangeEvent;

@Service
@ConditionalOnProperty(prefix = "ring.kafka", name = "mock-enabled", havingValue = "true", matchIfMissing = true)
public class LoggingStatusChangePublisher implements StatusChangePublisher {

	private static final Logger log = LoggerFactory.getLogger(LoggingStatusChangePublisher.class);

	private final StatusChangeEventHandler statusChangeEventHandler;

	private final RingOfflineProperties properties;

	private final ObjectMapper objectMapper;

	public LoggingStatusChangePublisher(StatusChangeEventHandler statusChangeEventHandler,
			RingOfflineProperties properties, ObjectMapper objectMapper) {
		this.statusChangeEventHandler = statusChangeEventHandler;
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	@Override
	public void publish(StatusChangeEvent event) {
		String topic = this.properties.getKafka().getTopics().getStatusChanges();
		String payload = serialize(event);
		log.info("[KAFKA-PUBLISH] topic={} partition=key:{} payload={}", topic, event.deviceId(), payload);
		log.info("[KAFKA-CONSUME] topic={} partition=key:{} payload={}", topic, event.deviceId(), payload);
		this.statusChangeEventHandler.onStatusChange(event);
	}

	private String serialize(StatusChangeEvent event) {
		try {
			return this.objectMapper.writeValueAsString(event);
		}
		catch (JsonProcessingException exception) {
			return event.toString();
		}
	}

}
