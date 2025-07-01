package com.kosta.readdam.repository;

import java.util.List;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;

public interface ClassRepositoryCustom {
	List<ClassCardDto> searchClasses(ClassSearchConditionDto condition);

}
