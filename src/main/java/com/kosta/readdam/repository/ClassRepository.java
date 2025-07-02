package com.kosta.readdam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer>, ClassRepositoryCustom  {

	Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable);

}
