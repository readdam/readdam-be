package com.kosta.readdam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.UserSimpleDto;
import com.kosta.readdam.repository.LibraryBookRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibraryServiceImpl implements LibraryService {
	private final LibraryBookRepositoryCustom libraryBookRepository;


    @Override
	public List<UserSimpleDto> getTop15UsersByIsbnFromLifeLibrary(String isbn) {
        return libraryBookRepository.findTop15UsersByIsbnAndLibraryName(isbn, "인생 책");
    }
}
