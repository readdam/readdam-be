package com.kosta.readdam.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.ClassEntity;


public interface ClassRepository extends JpaRepository<ClassEntity, Integer>  {

	Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable);
	List<ClassEntity> findTop4ByOrderByClassIdDesc(); // home 최신순 내림차순 + limit 4개 조회용 

	List<ClassEntity> findByLeader_Username(String username);
}
