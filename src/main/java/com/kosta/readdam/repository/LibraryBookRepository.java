package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Library;
import com.kosta.readdam.entity.LibraryBook;

public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {
    List<LibraryBook> findByLibrary_LibraryId(Long libraryId);
    void deleteByLibrary_LibraryId(Long libraryId);
	void deleteAllByLibrary(Library library);
}
