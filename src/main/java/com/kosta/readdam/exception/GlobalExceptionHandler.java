package com.kosta.readdam.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RoomHasReservationException.class)
    public ResponseEntity<Map<String, String>> handleRoomHasReservationException(RoomHasReservationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "room_has_reservation");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
