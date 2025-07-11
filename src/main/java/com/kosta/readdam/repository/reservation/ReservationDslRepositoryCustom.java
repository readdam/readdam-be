package com.kosta.readdam.repository.reservation;

import java.util.List;

import com.kosta.readdam.dto.PlaceReservInfoDto;


public interface ReservationDslRepositoryCustom {
	List<PlaceReservInfoDto> findAllPlaceReservations(String username);

}
