package com.kosta.readdam.external;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        String url = "https://dapi.kakao.com/v3/search/book?target=isbn&query=" + isbn;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoBookResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KakaoBookResponse.class);

        KakaoBookResponse result = response.getBody();

        if (result == null || result.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("카카오 API에서 책 정보를 찾을 수 없습니다.");
        }

        Document doc = result.getDocuments().get(0);

        return Book.builder()
                .bookIsbn(isbn)
                .bookImg(doc.getThumbnail())
                .bookIntro(doc.getContents())
                .pubDate(doc.getDatetime() != null ? doc.getDatetime().toLocalDate().toString() : null)
                .publisher(doc.getPublisher())
                .ranking(0)
                .rating(BigDecimal.ZERO)
                .title(doc.getTitle())
                .viewCnt(0)
                .writer(doc.getAuthors().isEmpty() ? "작자 미상" : doc.getAuthors().get(0))
                .build();
    }
}
