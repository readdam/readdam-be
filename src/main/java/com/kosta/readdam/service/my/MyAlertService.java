package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.entity.User;

public interface MyAlertService {
	
	List<AlertDto> getMyAlerts(String receiverUsername) throws Exception;
	void checkAlert(Integer alertId) throws Exception;
	
	long countUnread(String receiverUsername);

    List<AlertDto> getLatestAlerts(String receiverUsername, int limit);

}
