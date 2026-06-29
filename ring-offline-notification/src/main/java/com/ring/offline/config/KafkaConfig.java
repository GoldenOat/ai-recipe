package com.ring.offline.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	private final RingOfflineProperties properties;

	public KafkaConfig(RingOfflineProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnProperty(prefix = "ring.kafka", name = "mock-enabled", havingValue = "false")
	NewTopic heartbeatTopic() {
		return TopicBuilder.name(this.properties.getKafka().getTopics().getHeartbeats()).partitions(3).replicas(1)
			.build();
	}

	@Bean
	@ConditionalOnProperty(prefix = "ring.kafka", name = "mock-enabled", havingValue = "false")
	NewTopic statusChangeTopic() {
		return TopicBuilder.name(this.properties.getKafka().getTopics().getStatusChanges()).partitions(3).replicas(1)
			.build();
	}

}
