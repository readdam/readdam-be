package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	long countByPlaceRoom_PlaceRoomId(Integer placeRoomId);
	
	List<Reservation> findByCreatedAtBeforeAndStatus(LocalDateTime time, ReservationStatus status);

	List<Reservation> findByStatus(ReservationStatus pending);
	
	Optional<Reservation> findByClassEntity(ClassEntity classEntity);
}
