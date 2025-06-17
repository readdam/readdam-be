package com.kosta.readdam.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.kosta.readdam.entity.Book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

    private String bookIsbn;
    private Integer ranking;
    private String title;
    private String publisher;
    private OffsetDateTime pubDate;
    private String writer;
    private Integer reviewCnt;
    private Integer viewCnt;
    private BigDecimal rating;
    private String bookImg;
    private String bookIntro;

    public Book toEntity() {
        return Book.builder()
                .bookIsbn(bookIsbn)
                .ranking(ranking)
                .title(title)
                .publisher(publisher)
                .pubDate(pubDate)
                .writer(writer)
                .reviewCnt(reviewCnt)
                .viewCnt(viewCnt)
                .rating(rating)
                .bookImg(bookImg)
                .bookIntro(bookIntro)
                .build();
    }
}
