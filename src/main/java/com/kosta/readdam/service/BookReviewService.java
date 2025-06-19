package com.kosta.readdam.service;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.dto.book.BookReviewRequestDto;
import com.kosta.readdam.dto.book.BookReviewStatsDto;

public interface BookReviewService {
	Page<BookReviewDto> getReviews(String bookIsbn, String username, int page, int size);
	void writeReview(BookReviewRequestDto dto, String username);
	BookReviewStatsDto getReviewStats(String bookIsbn);
}
