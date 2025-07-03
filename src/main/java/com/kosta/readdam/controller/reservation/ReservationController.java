package com.kosta.readdam.controller.reservation;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.reservation.ReservationTimeResponse;
import com.kosta.readdam.service.reservation.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my/reservations")
public class ReservationController {
	private final ReservationService reservationService;

    @GetMapping("/availableTimes")
    public ResponseEntity<ReservationTimeResponse> getAvailableTimes(
            @RequestParam Integer placeRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        ReservationTimeResponse response = reservationService.getAvailableTimes(placeRoomId, date);
        return ResponseEntity.ok(response);
    }
}
