package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.BookList;
import com.kosta.readdam.entity.enums.BookListCategory;

@Repository
public interface BookListRepository extends JpaRepository<BookList, String> {

//	@Query("SELECT new com.kosta.readdam.dto.BookListDto(" + "   b.id, b.isbn, b.imageName, b.author, b.publisher, "
//			+ "   b.category, " +  "   bk.reviewCnt, bk.rating" + ") " + "FROM BookList b " + "JOIN Book bk ON b.isbn = bk.bookIsbn")
//	List<BookListDto> findAllWithReviewAndRating();
//
//	void deleteByCategory(BookListCategory category);

	@Transactional
	void deleteByCategory(BookListCategory category);

	List<BookList> findByCategory(BookListCategory category);
	
	long countByCategory(BookListCategory category);
}
