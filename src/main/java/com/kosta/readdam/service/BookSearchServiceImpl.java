package com.kosta.readdam.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kosta.readdam.dto.book.BookSearchResultDto;
import com.kosta.readdam.dto.kakao.KakaoBookResponse;
import com.kosta.readdam.external.KakaoBookApiClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookSearchServiceImpl implements BookSearchService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    
    private final KakaoBookApiClient kakaoBookApiClient;

    @Override
    public BookSearchResultDto searchBooks(String query, String target, String sort, int page, int size) {
    	 KakaoBookResponse result = kakaoBookApiClient.searchBooks(query, target, sort, page, size);

    	    if (result == null || result.getDocuments() == null) {
    	        return new BookSearchResultDto(Collections.emptyList(), 0, 0, true);
    	    }

    	    return new BookSearchResultDto(
    	        result.getDocuments(),
    	        result.getMeta().getTotalCount(),
    	        result.getMeta().getPageableCount(),
    	        result.getMeta().isEnd()
    	    );

    }
}
