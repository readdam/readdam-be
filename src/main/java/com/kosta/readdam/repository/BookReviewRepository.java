package com.kosta.readdam.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.BookReview;

public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    // 로그인 유저용
    @Query("SELECT r FROM BookReview r WHERE r.book.bookIsbn = :bookIsbn AND ( r.isHide = false OR r.user.username = :username ) ORDER BY r.regTime DESC")
    Page<BookReview> findVisibleOrOwnReviews(
        @Param("bookIsbn") String bookIsbn,
        @Param("username") String username,
        Pageable pageable
    );

    // 비로그인 유저용
    @Query("SELECT r FROM BookReview r WHERE r.book.bookIsbn = :bookIsbn AND r.isHide = false ORDER BY r.regTime DESC")
    Page<BookReview> findVisibleReviews(
        @Param("bookIsbn") String bookIsbn,
        Pageable pageable
    );

    List<BookReview> findByUserUsernameOrderByRegTimeDesc(String username);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM BookReview r WHERE r.book.bookIsbn = :bookIsbn")
    BigDecimal calculateAverageRating(@Param("bookIsbn") String bookIsbn);

    Integer countByBook_BookIsbn(String bookIsbn);
}
