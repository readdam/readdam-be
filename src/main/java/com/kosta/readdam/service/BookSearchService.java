package com.kosta.readdam.service;

import com.kosta.readdam.dto.book.BookSearchResultDto;

public interface BookSearchService {
	BookSearchResultDto searchBooks(String query, String target, String sort, int page, int size);
}
