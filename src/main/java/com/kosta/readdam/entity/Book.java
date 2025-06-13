package com.kosta.readdam.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kosta.readdam.dto.BookDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @Column(name = "book_isbn", length = 20, nullable = false)
    private String bookIsbn;

    @Column(nullable = false)
    private Integer ranking;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String publisher;

    @Column(name = "pub_date", nullable = false)
    private String pubDate;

    @Column(nullable = false)
    private String writer;

    @Column(name = "review_cnt")
    private Integer reviewCnt;

    @Column(name = "view_cnt")
    private Integer viewCnt;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "book_img")
    private String bookImg;

    @Column(name = "book_intro", columnDefinition = "TEXT")
    private String bookIntro;

    public BookDto toDto() {
        return BookDto.builder()
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
