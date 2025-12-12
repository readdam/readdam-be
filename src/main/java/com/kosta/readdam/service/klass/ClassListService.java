package com.kosta.readdam.service.klass;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.ClassListDto;

public interface ClassListService {
	Page<ClassListDto> getClassList(String keyword, String status, String period, Pageable pageable);
}
