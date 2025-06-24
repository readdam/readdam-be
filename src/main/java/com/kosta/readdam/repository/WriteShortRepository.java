package com.kosta.readdam.repository;

import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.WriteShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WriteShortRepository extends JpaRepository<WriteShort, Integer> {

    @Query(
        "SELECT new com.kosta.readdam.dto.WriteShortDto(e.title, w.content, COUNT(l)) " +
        "FROM WriteShort w " +
        "JOIN w.event e " +
        "LEFT JOIN WriteShortLike l ON l.writeShort = w " +
        "WHERE w.user.username = :username " +
        "GROUP BY e.title, w.content"
    )
    List<WriteShortDto> findDtosByUsername(@Param("username") String username);

}
