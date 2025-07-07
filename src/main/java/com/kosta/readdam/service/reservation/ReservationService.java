package com.kosta.readdam.service.reservation;

import java.time.LocalDate;
import java.util.List;

import com.kosta.readdam.dto.reservation.ReservationCreateRequest;
import com.kosta.readdam.dto.reservation.ReservationTimeResponse;

public interface ReservationService {
	ReservationTimeResponse getAvailableTimes(Integer placeRoomId, LocalDate date);
	void createReservations(String username, List<ReservationCreateRequest> requests);
}
