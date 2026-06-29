package com.ring.offline.notification;

import com.ring.offline.domain.StatusChangeEvent;

public interface NotificationService {

	void notify(StatusChangeEvent event);

}
