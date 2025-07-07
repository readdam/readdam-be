package com.kosta.readdam.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.book.BookSearchResultDto;
import com.kosta.readdam.dto.kakao.KakaoBookResponse;
import com.kosta.readdam.external.KakaoBookApiClient;
import com.kosta.readdam.repository.BookListDslRepository;
import com.kosta.readdam.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookSearchServiceImpl implements BookSearchService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final KakaoBookApiClient kakaoBookApiClient;
    private final BookRepository bookRepository;
    private final BookListDslRepository bookListDslRepository;

    @Override
    public BookSearchResultDto searchBooks(String query, String target, String sort, int page, int size) {
        KakaoBookResponse result = kakaoBookApiClient.searchBooks(query, target, sort, page, size);

        if (result == null || result.getDocuments() == null) {
            return new BookSearchResultDto(Collections.emptyList(), 0, 0, true);
        }

        result.getDocuments().forEach(doc -> {
            bookRepository.findById(doc.getIsbn())
                .ifPresentOrElse(
                    book -> {
                        doc.setRating(book.getRating() != null ? book.getRating().doubleValue() : 0.0);
                        doc.setReviewCnt(book.getReviewCnt() != null ? book.getReviewCnt() : 0);
                    },
                    () -> {
                        doc.setRating(0.0);
                        doc.setReviewCnt(0);
                    }
                );
        });

        return new BookSearchResultDto(
            result.getDocuments(),
            result.getMeta().getTotalCount(),
            result.getMeta().getPageableCount(),
            result.getMeta().isEnd()
        );
    }

	@Override
	public List<BookDto> searchForAll(String keyword, String sort, int limit) {
		KakaoBookResponse result = kakaoBookApiClient.searchBooks(keyword, null, sort, 1, limit);
	    
		if (result == null || result.getDocuments() == null) {
	        return Collections.emptyList();
	    }
		
	    return result.getDocuments().stream()
	            .map(BookDto::fromKakao)
	            .collect(Collectors.toList());
	}
}

