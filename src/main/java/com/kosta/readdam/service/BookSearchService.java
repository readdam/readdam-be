package com.kosta.readdam.service;

import java.util.List;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.SearchResultDto;
import com.kosta.readdam.dto.book.BookSearchResultDto;

public interface BookSearchService {
	BookSearchResultDto searchBooks(String query, String target, String sort, int page, int size);
	SearchResultDto<BookDto> searchForAll(String keyword, String sort, int limit); //통합검색용
}
