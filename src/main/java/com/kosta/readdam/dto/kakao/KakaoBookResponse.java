package com.kosta.readdam.dto.kakao;

import java.util.List;

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
        private String datetime;
    }

    @Data
    public static class Meta {
        private boolean isEnd;
        private int pageableCount;
        private int totalCount;
    }
}
