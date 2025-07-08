package com.kosta.readdam.service.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.reservation.ReservationCreateRequest;
import com.kosta.readdam.dto.reservation.ReservationDetailListDto;
import com.kosta.readdam.dto.reservation.ReservationTimeResponse;

public interface ReservationService {
	ReservationTimeResponse getAvailableTimes(Integer placeRoomId, LocalDate date);
	void createReservations(String username, List<ReservationCreateRequest> requests);
	Page<ReservationDetailListDto> getReservationPage(Pageable pageable, String date, String status, String keyword);
}
