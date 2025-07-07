package com.kosta.readdam.service.admin;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminAlertService {
	
	void sendCustomAlerts(
	        String senderUsername,
	        List<String> receiverUsernames,
	        String type,
	        String title,
	        String content,
	        String linkUrl,
	        LocalDateTime scheduledTime,
	        String imageName,
	        boolean sendToAll
	    );

}
