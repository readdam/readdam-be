package com.kosta.readdam.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import com.kosta.readdam.entity.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailListDto {
    private Integer reservationId;
    private String placeName;
    private String placeAddress;
    private String roomName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reserverName;
    private Integer participantCount;
    private ReservationStatus status;
}
