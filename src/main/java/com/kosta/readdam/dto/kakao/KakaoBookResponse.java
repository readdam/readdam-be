package com.kosta.readdam.dto.kakao;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class KakaoBookResponse {
    private List<Document> documents;

    @Data
    public static class Document {
        private String title;
        private List<String> authors;
        private String publisher;
        private String contents;
        private String thumbnail;
        private OffsetDateTime datetime;
    }
}