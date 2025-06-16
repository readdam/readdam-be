package com.kosta.readdam.dto.book;

import java.util.List;

import com.kosta.readdam.dto.kakao.KakaoBookResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookSearchResultDto {
    private List<KakaoBookResponse.Document> documents;
    private int totalCount;
}
