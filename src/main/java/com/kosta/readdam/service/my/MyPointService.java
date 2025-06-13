package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.PointDto;

public interface MyPointService {
	
	List<PointDto> getMyPointList(String username) throws Exception;

	void confirmAndChargePoint(String paymentKey, String orderId, int amount, String username);
}
