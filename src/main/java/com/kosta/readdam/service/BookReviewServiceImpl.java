package com.kosta.readdam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.entity.BookReview;
import com.kosta.readdam.repository.BookReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {
	private final BookReviewRepository bookReviewRepository;

	@Override
    public Page<BookReviewDto> getReviews(String bookIsbn, String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regTime").descending());
        return bookReviewRepository.findVisibleOrOwnReviews(bookIsbn, username, pageable)
//                .map(BookReview::toDto);
        		.map(BookReviewDto::fromEntity);

    }

}
