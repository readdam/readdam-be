package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Book;
import lombok.*;

import java.math.BigDecimal;

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
    private String pubDate;
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
