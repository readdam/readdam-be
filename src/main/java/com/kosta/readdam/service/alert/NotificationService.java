package com.kosta.readdam.service.alert;

import java.util.Map;

public interface NotificationService {

	void sendPush(String receiverUsername, String title, String body, Map<String, String> data);

	
}
