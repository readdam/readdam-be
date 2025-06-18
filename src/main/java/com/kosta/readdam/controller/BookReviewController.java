package com.kosta.readdam.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.service.BookReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book/reviews")
public class BookReviewController {
	 private final BookReviewService bookReviewService;

	    @GetMapping
	    public ResponseEntity<Page<BookReviewDto>> getReviews(
	            @RequestParam String bookIsbn,
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "5") int size,
	            @RequestParam(required = false) String username // 프론트에서 로그인한 유저 보내줘야 함
	    ) {
	        Page<BookReviewDto> reviews = bookReviewService.getReviews(bookIsbn, username, page, size);
	        return ResponseEntity.ok(reviews);
	    }
}
