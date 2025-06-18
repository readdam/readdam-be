package com.kosta.readdam.service;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.BookReviewDto;

public interface BookReviewService {
	Page<BookReviewDto> getReviews(String bookIsbn, String username, int page, int size);
}
