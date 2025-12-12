package com.kosta.readdam.repository.klass;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.ClassEntity;


public interface ClassRepository extends JpaRepository<ClassEntity, Integer>  {

	Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable);

	Page<ClassEntity> findByLeader_UsernameOrderByCreatedAtDesc(
	        String leaderUsername, Pageable pageable
	    );
}
