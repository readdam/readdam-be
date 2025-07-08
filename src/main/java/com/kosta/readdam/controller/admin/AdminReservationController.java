package com.kosta.readdam.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.reservation.ReservationDetailListDto;
import com.kosta.readdam.service.reservation.ReservationService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminReservationController {
	private final ReservationService reservationService;

	@GetMapping("/reservations")
	public ResponseEntity<Map<String, Object>> getReservations(
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String date,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String keyword
	) {
	    PageRequest pageable = PageRequest.of(page - 1, size);

	    Page<ReservationDetailListDto> reservationPage = reservationService.getReservationPage(pageable, date, status, keyword);

	    Map<String, Object> response = new HashMap<>();
	    response.put("content", reservationPage.getContent());
	    response.put("pageInfo", PageInfo2.from(reservationPage));

	    return ResponseEntity.ok(response);
	}

}
