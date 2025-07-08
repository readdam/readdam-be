package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.dto.SearchResultDto;

public interface ClassRepositoryCustom {
	Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable);
	SearchResultDto<ClassDto> searchForAll(String keyword, String sort, int limit); //통합검색용
}
