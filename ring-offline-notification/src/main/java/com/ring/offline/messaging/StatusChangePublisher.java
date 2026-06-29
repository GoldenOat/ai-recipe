package com.ring.offline.messaging;

import com.ring.offline.domain.StatusChangeEvent;

public interface StatusChangePublisher {

	void publish(StatusChangeEvent event);

}
