// MyBookLikeServiceImpl 클래스
package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.external.KakaoBookApiClient;
import com.kosta.readdam.repository.BookLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBookLikeServiceImpl implements MyBookLikeService {

    private final BookLikeRepository bookLikeRepository;
    private final KakaoBookApiClient kakaoBookApiClient;

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getLikedBooksByUsername(String username) throws Exception {
        return bookLikeRepository.findByUser_Username(username).stream()
            .map(like -> {
                // BookLike.entity 에 매핑된 Book 엔티티에서 ISBN 꺼내기
                String isbn = like.getBook().getBookIsbn();
                // 외부 API 호출로 최신 책 정보 가져오기
                Book kakaoBook = kakaoBookApiClient.fetchBookFromKakao(isbn);

                // Book → BookDto 변환
                return BookDto.builder()
                    .bookIsbn(kakaoBook.getBookIsbn())
                    .ranking(kakaoBook.getRanking())
                    .title(kakaoBook.getTitle())
                    .publisher(kakaoBook.getPublisher())
                    .pubDate(kakaoBook.getPubDate())
                    .writer(kakaoBook.getWriter())
                    .reviewCnt(kakaoBook.getReviewCnt())
                    .viewCnt(kakaoBook.getViewCnt())
                    .rating(kakaoBook.getRating())
                    .bookImg(kakaoBook.getBookImg())
                    .bookIntro(kakaoBook.getBookIntro())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
