package com.kosta.readdam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.BookReview;

public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {
	@Query("SELECT r FROM BookReview r WHERE r.book.bookIsbn = :bookIsbn AND (r.isHide = false OR r.user.username = :username) ORDER BY r.regTime DESC")
	    Page<BookReview> findVisibleOrOwnReviews(
	        @Param("bookIsbn") String bookIsbn,
	        @Param("username") String username,
	        Pageable pageable
	    );
}
