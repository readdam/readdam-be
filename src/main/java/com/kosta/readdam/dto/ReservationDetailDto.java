package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDetailDto {

    private Integer reservationDetailId;
    private Integer reservationId;
    private LocalDate date;
    private LocalTime time;

    public ReservationDetail toEntity(Reservation reservation) {
        return ReservationDetail.builder()
                .reservationDetailId(reservationDetailId)
                .reservation(reservation)
                .date(date)
                .time(time)
                .build();
    }
}
