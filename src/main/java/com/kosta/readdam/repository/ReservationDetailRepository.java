package com.kosta.readdam.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.ReservationDetail;

public interface ReservationDetailRepository extends JpaRepository<ReservationDetail, Integer> {

    List<ReservationDetail> findByReservationUserUsername(String username);
    
    List<ReservationDetail> findByDate(LocalDate date);
    
    @Query("SELECT rd.time FROM ReservationDetail rd JOIN rd.reservation r WHERE r.placeRoom.placeRoomId = :placeRoomId AND rd.date = :date")
        List<LocalTime> findReservedTimesByPlaceRoomIdAndDate(
                @Param("placeRoomId") Integer placeRoomId,
                @Param("date") LocalDate date
        );
    
    void deleteAllByReservation_ReservationId(Integer reservationId);
}
