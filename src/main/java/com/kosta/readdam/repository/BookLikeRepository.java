package com.kosta.readdam.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.entity.User;

public interface BookLikeRepository extends JpaRepository<BookLike, Integer> {
	Optional<BookLike> findByUserAndBook(User user, Book book);
	
	@Query("SELECT bl.book.bookIsbn FROM BookLike bl WHERE bl.user.username = :username AND bl.book.bookIsbn IN :isbnList")
	Set<String> findLikedIsbnSetByUserAndIsbnList(@org.springframework.data.repository.query.Param("username") String username,
	                                               @org.springframework.data.repository.query.Param("isbnList") List<String> isbnList);
	List<BookLike> findByUser_Username(String username);
	long countByBook(Book book);
	Collection<BookDto> findByUser(User user);
	boolean existsByUserUsernameAndBookBookIsbn(String username, String isbn);

}
