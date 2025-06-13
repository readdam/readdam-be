package com.kosta.readdam.entity;

import com.kosta.readdam.dto.ReservationDetailDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_detail_id", nullable = false, updatable = false)
    private Integer reservationDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    public ReservationDetailDto toDto() {
        return ReservationDetailDto.builder()
                .reservationDetailId(reservationDetailId)
                .reservationId(reservation.getReservationId())
                .date(date)
                .time(time)
                .build();
    }
}
