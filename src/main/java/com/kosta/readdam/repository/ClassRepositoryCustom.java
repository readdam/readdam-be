package com.kosta.readdam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;

public interface ClassRepositoryCustom {
	Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable);

}
