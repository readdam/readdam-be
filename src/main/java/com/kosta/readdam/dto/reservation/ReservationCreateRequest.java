package com.kosta.readdam.dto.reservation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateRequest {
	private Integer roomId;
    private Integer participantCount;
    private String reserverName;
    private String reserverPhone;
    private String requestMessage;
    private List<ReservationTimeRange> ranges;
}
