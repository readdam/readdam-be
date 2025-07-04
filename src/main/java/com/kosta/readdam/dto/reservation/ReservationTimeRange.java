package com.kosta.readdam.dto.reservation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationTimeRange {
	private String date; // yyyy-MM-dd
    private String start; // HH:mm
    private String end;   // HH:mm
    private List<String> times; // ["15:00","16:00","17:00"]
}
