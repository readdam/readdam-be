package com.kosta.readdam.external;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.kosta.readdam.dto.kakao.KakaoBookResponse;
import com.kosta.readdam.dto.kakao.KakaoBookResponse.Document;
import com.kosta.readdam.entity.Book;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class KakaoBookApiClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Book fetchBookFromKakao(String isbn) {
    	String queryIsbn = isbn.contains(" ") ? isbn.split(" ")[1] : isbn;
        String url = "https://dapi.kakao.com/v3/search/book?target=isbn&query=" + queryIsbn;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoBookResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KakaoBookResponse.class);

        KakaoBookResponse result = response.getBody();
        System.out.println(result);
        
        if (result == null || result.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("카카오 API에서 책 정보를 찾을 수 없습니다.");
        }

        Document doc = result.getDocuments().get(0);

        return Book.builder()
                .bookIsbn(isbn)
                .bookImg(doc.getThumbnail())
                .bookIntro(doc.getContents())
                .pubDate(doc.getDatetime() != null ? doc.getDatetime() : null)
                .publisher(doc.getPublisher())
                .ranking(0)
                .rating(BigDecimal.ZERO)
                .title(doc.getTitle())
                .viewCnt(0)
                .writer(doc.getAuthors().isEmpty() ? "작자 미상" : doc.getAuthors().get(0))
                .build();
    }

    public KakaoBookResponse searchBooks(String query, String target, String sort, int page, int size) {
    	UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v3/search/book")
                .queryParam("query", query)
                .queryParam("sort", sort)
                .queryParam("page", page)
                .queryParam("size", size);
    	 
        if (target != null && !target.isBlank()) {
            builder.queryParam("target", target);
        }

        URI url = builder.build().encode().toUri();
        System.out.println("요청 URI: " + builder.toUriString());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoBookResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KakaoBookResponse.class);

        KakaoBookResponse result = response.getBody();

        if (result != null && result.getMeta() != null) {
            System.out.println("총 검색 결과 수 (totalCount): " + result.getMeta().getTotalCount());
            System.out.println("페이지당 결과 수 (pageableCount): " + result.getMeta().getPageableCount());
            System.out.println("마지막 페이지 여부 (isEnd): " + result.getMeta().isEnd());
        } else {
            System.out.println("메타 정보가 없습니다.");
        }
        
        return response.getBody(); // documents + meta 모두 포함됨
    }
    
    public String fetchThumbnail(String isbn) {

        String queryIsbn = isbn.contains(" ") ? isbn.split(" ")[1] : isbn;
        String url = "https://dapi.kakao.com/v3/search/book?target=isbn&query=" + queryIsbn;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        ResponseEntity<KakaoBookResponse> resp =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), KakaoBookResponse.class);

        KakaoBookResponse body = resp.getBody();
        if (body == null || body.getDocuments().isEmpty())
            throw new IllegalArgumentException("카카오 썸네일 조회 실패: isbn=" + isbn);

        return body.getDocuments().get(0).getThumbnail();   // CDN URL
    }

    public String fetchIsbnString(String isbn) {

        String queryIsbn = isbn.contains(" ") ? isbn.split(" ")[1] : isbn;
        String url = "https://dapi.kakao.com/v3/search/book?target=isbn&query=" + queryIsbn;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        ResponseEntity<KakaoBookResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), KakaoBookResponse.class);

        KakaoBookResponse body = resp.getBody();
        if (body == null || body.getDocuments().isEmpty())
            throw new IllegalArgumentException("카카오 ISBN 조회 실패: isbn=" + isbn);

        // Kakao 응답의 Document.isbn → "10자리␣13자리" 형식
        return body.getDocuments().get(0).getIsbn();
    }

}
