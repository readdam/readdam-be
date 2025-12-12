package com.kosta.readdam.repository.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.reservation.ReservationDetailListDto;

public interface ReservationDslRepository {
	Page<ReservationDetailListDto> findReservations(Pageable pageable, String dateStr, String statusStr, String keyword);
}