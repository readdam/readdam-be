package com.kosta.readdam.dto.book;

import java.util.List;

import com.kosta.readdam.dto.kakao.KakaoBookResponse.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookSearchResultDto {

    private List<Document> documents;
    private int totalCount;
    private int pageableCount;
    private boolean isEnd;
    
    public BookSearchResultDto(List<Document> documents, int totalCount, int pageableCount, boolean isEnd) {
        this.documents = documents;
        this.totalCount = totalCount;
        this.pageableCount = pageableCount;
        this.isEnd = isEnd;
    }

    public BookSearchResultDto(List<Document> documents) {
        this.documents = documents;
        this.totalCount = documents != null ? documents.size() : 0;
    }

    public BookSearchResultDto(List<Document> documents, int totalCount) {
        this.documents = documents;
        this.totalCount = totalCount;
    }
}
