package com.kosta.readdam.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.kakao.KakaoBookResponse;
import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.external.KakaoBookApiClient;
import com.kosta.readdam.repository.BookLikeRepository;
import com.kosta.readdam.repository.BookRepository;
import com.kosta.readdam.repository.UserRepository;

@Service
public class BookLikeServiceImpl implements BookLikeService {

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

		Book book = bookRepository.findById(bookIsbn).orElseGet(() -> {
			Book newBook = kakaoBookApiClient.fetchBookFromKakao(bookIsbn);
			return bookRepository.save(newBook);
		});

		// 좋아요 했으면 삭제, 안 했으면 추가
		return bookLikeRepository.findByUserAndBook(user, book).map(existingLike -> {
			bookLikeRepository.delete(existingLike);
			return false; // false → 취소됨
		}).orElseGet(() -> {
			BookLike bookLike = BookLike.builder().user(user).book(book).date(LocalDateTime.now()).build();
			bookLikeRepository.save(bookLike);
			return true; // true → 새로 좋아요 됨
		});
	}
	
	public List<String> getLikedIsbns(String username, String query, String target, String sort, int page, int size) {
        KakaoBookResponse kakaoResult = kakaoBookApiClient.searchBooks(query, target, sort, page, size);
        List<KakaoBookResponse.Document> docs = kakaoResult.getDocuments();
        
        List<String> isbnList = docs.stream()
        	    .map(KakaoBookResponse.Document::getIsbn)
        	    .filter(Objects::nonNull)
        	    .map(String::trim)
        	    .collect(Collectors.toList());


        System.out.println("username: " + username);
        System.out.println("isbnList: " + isbnList);

        Set<String> likedIsbnSet = bookLikeRepository.findLikedIsbnSetByUserAndIsbnList(username, isbnList);

        System.out.println("likedIsbnSet: " + likedIsbnSet);
        System.out.println("isbnList: ");
        isbnList.forEach(System.out::println);

        return docs.stream()
        	    .filter(doc -> {
        	        String isbn = doc.getIsbn();
        	        return isbn != null && likedIsbnSet.contains(isbn.trim());
        	    })
        	    .map(doc -> doc.getIsbn().trim())
        	    .collect(Collectors.toList());
    }
	
	@Override
	public boolean isBookLiked(String username, String isbn) {
	    if (isbn == null || isbn.trim().isEmpty()) {
	        return false;
	    }
	    return bookLikeRepository.existsByUserUsernameAndBookBookIsbn(username, isbn.trim());
	}

}
