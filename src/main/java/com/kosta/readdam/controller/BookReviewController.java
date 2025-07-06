package com.kosta.readdam.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.dto.book.BookReviewPageResponse;
import com.kosta.readdam.dto.book.BookReviewRequestDto;
import com.kosta.readdam.dto.book.BookReviewUpdateRequestDto;
import com.kosta.readdam.service.BookReviewService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book/reviews")
public class BookReviewController {
	private final BookReviewService bookReviewService;

	// 리뷰 작성
    @PostMapping
    public ResponseEntity<BookReviewDto> writeReview(
            @RequestBody BookReviewRequestDto dto,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        BookReviewDto saved = bookReviewService.writeReview(dto, username);
        return ResponseEntity.ok(saved);
    }

    // 리뷰 조회
    @GetMapping
    public ResponseEntity<BookReviewPageResponse> getReviews(
            @RequestParam String bookIsbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal != null ? principal.getUsername() : null;
        Page<BookReviewDto> reviewPage = bookReviewService.getReviews(bookIsbn, username, page, size);

        BookReviewPageResponse response = BookReviewPageResponse.builder()
                .content(reviewPage.getContent())
                .pageInfo(PageInfo2.from(reviewPage))
                .build();

        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @RequestBody BookReviewUpdateRequestDto dto,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        bookReviewService.updateReview(id, username, dto.getComment(), dto.getRating(), dto.getIsHide());
        return ResponseEntity.ok().body("리뷰가 수정되었습니다.");
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Integer id,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        bookReviewService.deleteReview(id, username);
        return ResponseEntity.ok().body("리뷰가 삭제되었습니다.");
    }

}
