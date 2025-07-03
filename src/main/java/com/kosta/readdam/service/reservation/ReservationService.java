package com.kosta.readdam.service.reservation;

import java.time.LocalDate;

import com.kosta.readdam.dto.reservation.ReservationTimeResponse;

public interface ReservationService {
	ReservationTimeResponse getAvailableTimes(Integer placeRoomId, LocalDate date);
}
