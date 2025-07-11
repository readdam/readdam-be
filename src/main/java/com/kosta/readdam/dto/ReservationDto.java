package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

    private Integer reservationId;
    private String username;
    private Integer placeRoomId;
    private Integer participantCount;
    private String reserverName;
    private String reserverPhone;
    private String requestMessage;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public Reservation toEntity(User user, PlaceRoom placeRoom) {
        return Reservation.builder()
                .reservationId(reservationId)
                .user(user)
                .placeRoom(placeRoom)
                .participantCount(participantCount)
                .reserverName(reserverName)
                .reserverPhone(reserverPhone)
                .requestMessage(requestMessage)
                .status(status != null ? status : ReservationStatus.PENDING)
                .createdAt(createdAt)
                .build();
    }
}
