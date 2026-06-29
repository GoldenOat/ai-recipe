package com.ring.offline.messaging;

public interface EventPublisher {

	void publish(String topic, Object event);

}
