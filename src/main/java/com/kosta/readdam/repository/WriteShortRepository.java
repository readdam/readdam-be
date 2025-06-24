package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.WriteShort;

public interface WriteShortRepository extends JpaRepository<WriteShort, Integer> {

//    @Query(
//        "SELECT new com.kosta.readdam.dto.WriteShortDto(e.title, w.content, COUNT(l)) " +
//        "FROM WriteShort w " +
//        "JOIN w.event e " +
//        "LEFT JOIN WriteShortLike l ON l.writeShort = w " +
//        "WHERE w.user.username = :username " +
//        "GROUP BY e.title, w.content"
//    )
//    List<WriteShortDto> findDtosByUsername(@Param("username") String username);

    Page<WriteShort> findByEvent_EventIdAndIsHideFalse(Integer eventId, Pageable pageable);
}