package com.kosta.readdam.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewStatsDto {
    private String bookIsbn;
    private long reviewCount;
    private Double averageRating;
    
    public BookReviewStatsDto(String bookIsbn, long reviewCount, double averageRating) {
        this.bookIsbn = bookIsbn;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }
}