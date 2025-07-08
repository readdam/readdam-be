package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.dto.PagedResponse;

public interface MyAlertService {
	
	PagedResponse<AlertDto> getMyAlerts(String receiverUsername, int page, int size) ;
	
	void checkAlert(Integer alertId) throws Exception;
	
	long countUnread(String receiverUsername);

    List<AlertDto> getLatestAlerts(String receiverUsername, int limit);

}
