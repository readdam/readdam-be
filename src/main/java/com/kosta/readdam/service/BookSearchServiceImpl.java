package com.kosta.readdam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import com.kosta.readdam.dto.book.BookSearchResultDto;
import com.kosta.readdam.dto.kakao.KakaoBookResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookSearchServiceImpl implements BookSearchService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public BookSearchResultDto searchBooks(String query, String target, String sort, int page, int size) {
        String url = "https://dapi.kakao.com/v3/search/book";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)
                .queryParam("sort", sort)
                .queryParam("page", page)
                .queryParam("size", size);
        if (target != null && !target.isBlank()) {
            builder.queryParam("target", target);
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoBookResponse> response = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, entity, KakaoBookResponse.class
        );

        KakaoBookResponse result = response.getBody();
        if (result == null || result.getDocuments().isEmpty()) {
            return new BookSearchResultDto(List.of(), 0);
        }

        return new BookSearchResultDto(result.getDocuments(), result.getMeta().getPageableCount());
    }
}
