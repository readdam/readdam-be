package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {

    // 현재 진행 중인 이벤트 하나
    @Query("SELECT e FROM Event e WHERE e.startTime <= :now AND e.endTime >= :now")
    Optional<Event> findCurrentEvent(@Param("now") LocalDateTime now);

    // 다가오는 이벤트 (startTime > now)
    List<Event> findByStartTimeAfter(LocalDateTime now);

    // 진행 중 이벤트 (startTime <= now AND endTime >= now)
    @Query("SELECT e FROM Event e WHERE e.startTime <= :now AND e.endTime >= :now")
    List<Event> findOngoingEvents(@Param("now") LocalDateTime now);

    // 완료된 이벤트 (endTime < now)
    List<Event> findByEndTimeBefore(LocalDateTime now);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.eventId = :id")
    Optional<Event> findByIdWithLock(@Param("id") Integer eventId);
}
