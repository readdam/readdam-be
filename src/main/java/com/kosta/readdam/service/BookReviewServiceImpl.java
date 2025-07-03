package com.kosta.readdam.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.dto.book.BookReviewRequestDto;
import com.kosta.readdam.dto.book.BookReviewStatsDto;
import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookReview;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.external.KakaoBookApiClient;
import com.kosta.readdam.repository.BookRepository;
import com.kosta.readdam.repository.BookReviewRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {
	private final BookReviewRepository bookReviewRepository;
	private final BookRepository bookRepository;
    private final UserRepository userRepository;
	private final KakaoBookApiClient kakaoBookApiClient;

	@Override
    public Page<BookReviewDto> getReviews(String bookIsbn, String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regTime").descending());
        return bookReviewRepository.findVisibleOrOwnReviews(bookIsbn, username, pageable)
//                .map(BookReview::toDto);
        		.map(BookReviewDto::fromEntity);

    }

	@Override
	@Transactional
    public void writeReview(BookReviewRequestDto dto, String username) {
	 	User user = userRepository.findById(username)
	 			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
	 	
	 	Book book = bookRepository.findById(dto.getBookIsbn()).orElseGet(() -> {
	        Book newBook = kakaoBookApiClient.fetchBookFromKakao(dto.getBookIsbn());
	        return bookRepository.save(newBook);
	    });

	 	// 1. 리뷰 저장
        BookReview review = new BookReview();
        review.setBook(book);
        review.setUser(user);
        review.setComment(dto.getComment());
        review.setIsHide(dto.getIsHide());
        review.setRating(dto.getRating());
        review.setRegTime(LocalDateTime.now());

        bookReviewRepository.save(review);
        
        // 2. 리뷰 집계
        updateBookReviewStats(dto.getBookIsbn());
    }
	 
	 @Override
	 public BookReviewStatsDto getReviewStats(String bookIsbn) {
	        return bookReviewRepository.findStatsByBookIsbn(bookIsbn);
	 }
	 
	 public void updateBookReviewStats(String bookIsbn) {
		    // 평점 평균과 개수 구하기
		 	BigDecimal avgRating = bookReviewRepository.calculateAverageRating(bookIsbn);
		 	if (avgRating == null) avgRating = BigDecimal.ZERO;
		 	Integer reviewCount = bookReviewRepository.countByBook_BookIsbn(bookIsbn);

		    // Book 업데이트
		    bookRepository.updateRatingAndCount(bookIsbn, avgRating, reviewCount);
	}
}
