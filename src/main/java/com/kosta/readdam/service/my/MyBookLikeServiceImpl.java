// src/main/java/com/kosta/readdam/service/my/MyBookLikeServiceImpl.java
package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.BookLikeRepository;
import com.kosta.readdam.repository.BookRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBookLikeServiceImpl implements MyBookLikeService {

    private final UserRepository       userRepository;
    private final BookLikeRepository   bookLikeRepository;
    private final BookRepository       bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getLikedBooksByUsername(String username) {
        // 1) 사용자 검증
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + username));

        // 2) 좋아요 관계에서 ISBN 목록 추출
        List<String> isbns = bookLikeRepository
            .findByUser_Username(username)
            .stream()
            .map(bl -> bl.getBook().getBookIsbn())
            .collect(Collectors.toList());

        if (isbns.isEmpty()) {
            return List.of();
        }

        // 3) ISBN 목록으로 Book 엔티티 한 번에 조회
        List<Book> books = bookRepository.findAllById(isbns);

        // 4) DTO 변환 후 반환
        return books.stream()
            .map(b -> BookDto.builder()
                .bookIsbn(b.getBookIsbn())
                .title(b.getTitle())
                .writer(b.getWriter())
                .publisher(b.getPublisher())
                .pubDate(b.getPubDate())
                .reviewCnt(b.getReviewCnt())
                .viewCnt(b.getViewCnt())
                .rating(b.getRating())
                .bookImg(b.getBookImg())
                .bookIntro(b.getBookIntro())
                .build()
            )
            .collect(Collectors.toList());
    }
}
