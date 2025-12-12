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
    public BookReviewDto writeReview(BookReviewRequestDto dto, String username) {
	 	User user = userRepository.findById(username)
	 			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
	 	
	 	Book book = bookRepository.findById(dto.getBookIsbn()).orElseGet(() -> {
	        Book newBook = kakaoBookApiClient.fetchBookFromKakao(dto.getBookIsbn());
	        return bookRepository.save(newBook);
	    });

	 	// 1. 리뷰 저장
	 	BookReview review = BookReview.builder()
	            .book(book)
	            .user(user)
	            .comment(dto.getComment())
	            .isHide(dto.getIsHide() != null ? dto.getIsHide() : false)
	            .rating(dto.getRating())
	            .regTime(LocalDateTime.now())
	            .build();

	    BookReview saved = bookReviewRepository.save(review);
	    
        bookReviewRepository.save(review);
        
        // 2. 리뷰 집계
        updateBookReviewStats(dto.getBookIsbn());
        
        return BookReviewDto.fromEntity(saved);
    }
	 
	 
	 public void updateBookReviewStats(String bookIsbn) {
		    // 평점 평균과 개수 구하기
		 	BigDecimal avgRating = bookReviewRepository.calculateAverageRating(bookIsbn);
		 	if (avgRating == null) avgRating = BigDecimal.ZERO;
		 	Integer reviewCount = bookReviewRepository.countByBook_BookIsbn(bookIsbn);

		    // Book 업데이트
		    bookRepository.updateRatingAndCount(bookIsbn, avgRating, reviewCount);
	}
	 
	 @Override
	 @Transactional
	 public void updateReview(Integer reviewId, String username, String comment, Number rating, Boolean isHide) {
	     BookReview review = bookReviewRepository.findById(reviewId)
	             .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

	     if (!review.getUser().getUsername().equals(username)) {
	         throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
	     }

	     review.setComment(comment);
	     review.setRating(rating instanceof BigDecimal ? (BigDecimal) rating : BigDecimal.valueOf(rating.doubleValue()));
	     review.setIsHide(isHide != null ? isHide : false);

	     // 평점 집계 업데이트
	     updateBookReviewStats(review.getBook().getBookIsbn());
	 }

	 @Override
	 @Transactional
	 public void deleteReview(Integer reviewId, String username) {
	     BookReview review = bookReviewRepository.findById(reviewId)
	             .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

	     if (!review.getUser().getUsername().equals(username)) {
	         throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
	     }

	     String bookIsbn = review.getBook().getBookIsbn();

	     bookReviewRepository.delete(review);

	     // 평점 집계 업데이트
	     updateBookReviewStats(bookIsbn);
	 }

}
