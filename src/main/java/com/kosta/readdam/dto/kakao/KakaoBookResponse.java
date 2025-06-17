package com.kosta.readdam.dto.kakao;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
public class KakaoBookResponse {
    private List<Document> documents;
    private Meta meta;

    @Data
    public static class Document {
        private String title;
        private List<String> authors;
        private String publisher;
        private String thumbnail;
        private String contents;
        private String isbn;
        private OffsetDateTime datetime;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Meta {
        private boolean isEnd;
        private int pageableCount;
        private int totalCount;
    }
    
}
