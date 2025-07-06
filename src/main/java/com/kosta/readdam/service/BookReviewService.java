package com.kosta.readdam.service;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.dto.book.BookReviewRequestDto;

public interface BookReviewService {
	Page<BookReviewDto> getReviews(String bookIsbn, String username, int page, int size);
	BookReviewDto writeReview(BookReviewRequestDto dto, String username);
    void updateReview(Integer reviewId, String username, String comment, Number rating, Boolean isHide);
    void deleteReview(Integer reviewId, String username);
}
