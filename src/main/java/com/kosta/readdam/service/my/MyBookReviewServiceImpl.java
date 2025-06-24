package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.entity.Book;
import com.kosta.readdam.repository.BookReviewRepository;
import com.kosta.readdam.external.KakaoBookApiClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBookReviewServiceImpl implements MyBookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final KakaoBookApiClient kakaoBookApiClient;

    @Override
    @Transactional(readOnly = true)
    public List<BookReviewDto> getReviewsByUsername(String username) throws Exception{
        return bookReviewRepository.findByUserUsernameOrderByRegTimeDesc(username)
            .stream()
            .map(review -> {
                String isbn = review.getBook().getBookIsbn();
                Book kakaoBook = kakaoBookApiClient.fetchBookFromKakao(isbn);

                BookDto bookDto = BookDto.builder()
                        .bookIsbn(kakaoBook.getBookIsbn())
                        .title(kakaoBook.getTitle())
                        .bookImg(kakaoBook.getBookImg())
                        .writer(kakaoBook.getWriter())
                        .publisher(kakaoBook.getPublisher())
                        .build();

                return BookReviewDto.builder()
                        .bookReviewId(review.getBookReviewId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .regTime(review.getRegTime())
                        .isHide(review.getIsHide())
                        .book(bookDto)
                        .build();
            })
            .collect(Collectors.toList());
    }
}
