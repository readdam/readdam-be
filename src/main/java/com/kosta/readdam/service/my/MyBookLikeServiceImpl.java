// src/main/java/com/kosta/readdam/service/my/MyBookLikeServiceImpl.java
package com.kosta.readdam.service.my;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.BookLikeRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBookLikeServiceImpl implements MyBookLikeService {

    private final UserRepository     userRepository;
    private final BookLikeRepository bookLikeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getLikedBooksByUsername(String username, int page, int size) {
        // 1) 사용자 검증
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + username));

        // 2) Pageable 생성 (최신 좋아요 순)
        PageRequest pageable = PageRequest.of(page, size, Sort.by("date").descending());

        // 3) 페이징된 BookLike 조회 후 DTO 매핑
        return bookLikeRepository.findByUser_Username(username, pageable)
            .map(bl -> {
                var b = bl.getBook();
                return BookDto.builder()
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
                    .build();
            });
    }
}
