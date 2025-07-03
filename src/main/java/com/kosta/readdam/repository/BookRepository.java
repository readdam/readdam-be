package com.kosta.readdam.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Book;

public interface BookRepository extends JpaRepository<Book, String> {

	@Modifying
	@Query("UPDATE Book b SET b.rating = :rating, b.reviewCnt = :reviewCnt WHERE b.bookIsbn = :bookIsbn")
	void updateRatingAndCount(@Param("bookIsbn") String bookIsbn,
	                          @Param("rating") BigDecimal rating,
	                          @Param("reviewCnt") Integer reviewCnt);

}
