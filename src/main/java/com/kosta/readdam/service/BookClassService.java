package com.kosta.readdam.service;

import java.util.List;

import com.kosta.readdam.dto.ClassDto;

public interface BookClassService {
	List<ClassDto> findClassesByBook(String title, String author);
}
