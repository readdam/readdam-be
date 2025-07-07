package com.kosta.readdam.dto.book;

import java.util.List;

import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.util.PageInfo2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookReviewPageResponse {
    private List<BookReviewDto> content;
    private PageInfo2 pageInfo;
}	
