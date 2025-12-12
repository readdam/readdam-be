package com.kosta.readdam.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.ClassListDto;

// 관리자 모임 목록
public interface ClassListRepositoryCustom {
	Page<ClassListDto> searchClasses(String keyword, String status,
            LocalDate fromDate, LocalDate toDate, Pageable pageable);

	long countClasses(String titleKeyword, String leaderKeyword, String status,
            LocalDate fromDate, LocalDate toDate);
}
