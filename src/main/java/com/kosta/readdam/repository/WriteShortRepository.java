package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.WriteShort;

public interface WriteShortRepository extends JpaRepository<WriteShort, Integer> {
	
    Page<WriteShort> findByEvent_EventIdAndIsHideFalse(Integer eventId, Pageable pageable);
    
    List<WriteShort> findByUser_UsernameAndIsHideFalseOrderByRegDateDesc(String username);
    
    List<WriteShort> findTop3ByEvent_EventIdAndIsHideFalseOrderByLikesDesc(Integer eventId);

}