package com.kosta.readdam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

}
