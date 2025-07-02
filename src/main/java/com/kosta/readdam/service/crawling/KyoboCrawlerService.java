package com.kosta.readdam.service.crawling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.entity.BookList;
import com.kosta.readdam.entity.enums.BookListCategory;
import com.kosta.readdam.external.KakaoBookApiClient;
import com.kosta.readdam.repository.BookListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ▸ 교보 공개 베스트셀러 목록 API로 기본 정보 수집
 * ▸ ISBN이 없으면 cmdtCode 사용
 * ▸ 제목 / 썸네일이 비어 있으면 ISBN 기반 Kakao 검색 API로 보강
 * ▸ 상세 API 호출 X
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KyoboCrawlerService {

    private final BookListRepository repo;
    private final KakaoBookApiClient kakao;

    /* ───────── API & 공통 ───────── */
    private static final String LIST_API =
        "https://store.kyobobook.co.kr/api/gw/best/best-seller/total"
      + "?period=%s&page=%d&per=20&bsslBksClstCode=A";

    private static final int TIMEOUT = 15_000;
    private static final ObjectMapper OM = new ObjectMapper();

    /* ───────── 퍼블릭 메서드 ───────── */

    public List<BookList> fetchBookList(BookListCategory cat) {
        try {
            return crawlList(cat);
        } catch (IOException e) {
            throw new RuntimeException("Kyobo 목록 크롤링 실패", e);
        }
    }

    @Transactional
    public void importByCategory(BookListCategory cat) {
        try {
            List<BookList> list = crawlList(cat);
            repo.deleteByCategory(cat);
            repo.saveAll(list);
            log.info("[Kyobo] {}: {}권 저장 완료", cat, list.size());
        } catch (IOException e) {
            throw new RuntimeException("Kyobo 저장 실패", e);
        }
    }

    /* ───────── 내부 로직 ───────── */

    private String period(BookListCategory c) {
        switch (c) {
            case DAILY:   return "002";
            case WEEKLY:  return "003";
            case MONTHLY: return "004";
            default: throw new IllegalArgumentException("지원하지 않는 카테고리");
        }
    }

    private List<BookList> crawlList(BookListCategory cat) throws IOException {

        List<BookList> list = new ArrayList<>();
        String p = period(cat);

        for (int page = 1; ; page++) {

            String url  = String.format(LIST_API, p, page);
            String body = Jsoup.connect(url)
                               .ignoreContentType(true)
                               .timeout(TIMEOUT)
                               .userAgent("Mozilla/5.0")
                               .referrer("https://store.kyobobook.co.kr/")
                               .get()
                               .body()
                               .text();

            JsonNode arr = OM.readTree(body)
                             .path("data").path("bestSeller");
            if (arr.isEmpty()) break;

            for (JsonNode n : arr)
                list.add(toEntity(n, cat));
        }
        log.info("[Kyobo] {}: {}권 수집 완료", cat, list.size());
        return list;
    }

    /** 목록 JSON → BookList (제목 / 이미지 Kakao 보강) */
    private BookList toEntity(JsonNode n, BookListCategory cat) {

        String saleId = n.path("saleCmdtid").asText();

        /* ---------- ISBN ---------- */
        String isbn = n.path("isbn13").asText(null);
        if (isbn == null || isbn.isBlank())
            isbn = n.path("cmdtCode").asText(null);

        /* ---------- 제목 ---------- */
        String title = n.path("cmdtName").asText(null);
       

        /* ---------- 이미지(썸네일) ---------- */
        String img = null;
        if (isbn != null && !isbn.isBlank()) {
            try {
                img = kakao.fetchThumbnail(isbn);          
            } catch (Exception e) {
                log.debug("[Kakao] 썸네일 보강 실패 isbn={}", isbn);
            }
        }
        
        
        Integer ranking = n.path("prstRnkn").isInt()
                ? n.path("prstRnkn").asInt()
                : null;


        return BookList.builder()
                .id(saleId + "_" + cat.name())
                .title(title)
                .isbn(isbn)
                .imageName(img)            
                .author(n.path("chrcName").asText(null))
                .publisher(n.path("pbcmName").asText(null))
                .ranking(ranking)
                .category(cat)
                .build();
    }
    
    public boolean isEmpty(BookListCategory cat) {
        return repo.countByCategory(cat) == 0;
    }
}
