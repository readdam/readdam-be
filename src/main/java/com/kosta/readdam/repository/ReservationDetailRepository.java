package com.kosta.readdam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ReservationDetail;

public interface ReservationDetailRepository extends JpaRepository<ReservationDetail, Integer> {

    List<ReservationDetail> findByReservationUserUsername(String username);
    
    List<ReservationDetail> findByDate(LocalDate date);
}
