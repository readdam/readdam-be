package com.kosta.readdam.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.repository.BookClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookClassServiceImpl implements BookClassService {

	private final BookClassRepository classRepository;
	 
	@Override
    public List<ClassDto> findClassesByBook(String title, String author) {
        List<ClassEntity> entities = classRepository.findByBookTitleAndAuthor(title, author);
        return entities.stream()
        		.map(ClassEntity::toDto)
                .collect(Collectors.toList());
    }
}
