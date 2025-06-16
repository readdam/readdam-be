package com.kosta.readdam.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.external.KakaoBookApiClient;
import com.kosta.readdam.repository.BookLikeRepository;
import com.kosta.readdam.repository.BookRepository;
import com.kosta.readdam.repository.UserRepository;

@Service
public class BookLikeserviceImpl implements BookLikeService {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private BookLikeRepository bookLikeRepository;
	
	@Autowired
	private KakaoBookApiClient kakaoBookApiClient;
	
	@Transactional
	public boolean toggleLike(String username, String bookIsbn) {
	    User user = userRepository.findById(username)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

	    Book book = bookRepository.findById(bookIsbn)
	            .orElseGet(() -> {
	                Book newBook = kakaoBookApiClient.fetchBookFromKakao(bookIsbn);
	                return bookRepository.save(newBook);
	            });

	    // 좋아요 했으면 삭제, 안 했으면 추가
	    return bookLikeRepository.findByUserAndBook(user, book)
	            .map(existingLike -> {
	                bookLikeRepository.delete(existingLike);
	                return false; // false → 취소됨
	            })
	            .orElseGet(() -> {
	                BookLike bookLike = BookLike.builder()
	                        .user(user)
	                        .book(book)
	                        .date(LocalDateTime.now())
	                        .build();
	                bookLikeRepository.save(bookLike);
	                return true; // true → 새로 좋아요 됨
	            });
	}


}
