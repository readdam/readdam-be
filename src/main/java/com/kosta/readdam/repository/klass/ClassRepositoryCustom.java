package com.kosta.readdam.repository.klass;

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
	List<ClassCardDto> findTopNClasses(int limit); //홈 모임 최신용
	List<ClassCardDto> findTopNClassesByDistance(double lat, double lng, int limit); //홈 모임 거리용
}
