package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.entity.User;

public interface BookLikeRepository extends JpaRepository<BookLike, Integer> {
	Optional<BookLike> findByUserAndBook(User user, Book book);
}
