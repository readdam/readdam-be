package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {
	// 최신 이벤트 가져오기
    @Query("SELECT e FROM Event e WHERE e.startTime <= :now AND e.endTime >= :now")
    Optional<Event> findCurrentEvent(@Param("now") LocalDateTime now);

}
