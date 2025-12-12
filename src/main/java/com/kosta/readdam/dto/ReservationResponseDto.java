package com.kosta.readdam.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.kosta.readdam.entity.enums.ReservationStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDto {
    private Integer reservationId;
    private String placeName;  
    private String basicAddress;
    private String detailAddress;   
    private LocalDate date;
    private LocalTime time;
    private Integer participantCount;
    private String image;    
    private String timeRange;
    private ReservationStatus status;
    private Integer placeId;
}
