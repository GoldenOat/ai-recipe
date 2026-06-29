package com.ring.offline.messaging;

import com.ring.offline.domain.HeartbeatEvent;

public interface HeartbeatEventHandler {

	void onHeartbeat(HeartbeatEvent event);

}
