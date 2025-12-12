package com.kosta.readdam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 조회 조건용 dto 추가
public class WriteSearchRequestDto { 
    private String type;       // 글 유형
    private String status;     // 첨삭 상태
    private String orderBy;    // 정렬 기준
    private String keyword;    // 키워드 검색
    private int page = 1;      // 페이지 번호 (기본값 1) //null 없으니 int로 함
    private String sort = "recent"; // 🔥 정렬 기본값 추가 ("recent" or "view")
}

