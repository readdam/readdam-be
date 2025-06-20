package com.kosta.readdam.dto.book;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewRequestDto {
    private String comment;
    private Boolean isHide;
    private BigDecimal rating;
    private String bookIsbn;
    private String username;
}
