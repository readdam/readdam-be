package com.kosta.readdam.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewUpdateRequestDto {
    private String comment;
    private Integer rating;
    private Boolean isHide;
}
