package com.kosta.readdam.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchResultDto<T> {
	// 통합검색 - 각 카테고리별 단독 검색 결과용
    private List<T> content;
    private int totalCount;
}
