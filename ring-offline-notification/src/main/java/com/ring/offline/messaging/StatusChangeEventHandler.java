package com.ring.offline.messaging;

import com.ring.offline.domain.StatusChangeEvent;

public interface StatusChangeEventHandler {

	void onStatusChange(StatusChangeEvent event);

}
