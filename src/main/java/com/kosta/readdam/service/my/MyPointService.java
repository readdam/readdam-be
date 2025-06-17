package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.PointDto;
import com.kosta.readdam.entity.User;

public interface MyPointService {
	
	List<PointDto> getMyPointList(String username) throws Exception;

	int getMyTotalPoint(String username) throws Exception;

	String createOrder(User user, int point, int price) throws Exception;

	void verifyAndSave(String paymentKey, String orderUuid, int point, User user);
}
