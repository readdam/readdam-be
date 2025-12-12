package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.ReservationResponseDto;

public interface MyReservationService {

	List<ReservationResponseDto> getReservations(String username) throws Exception;

	void cancelReservation(String username, Integer reservationId) throws Exception;

}
