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
    
    // 통합검색용 필드 추가    
    private String cover;      // 카카오 썸네일 URL
    private String authors;    // 카카오 authors
    private String searchType;  // 통합검색 타입 구분 (BOOK)
    private String image; // 통합검색용
    
    // 통합검색 전용 생성자   
    public BookDto(
            String bookIsbn,
            String title,
            String cover,
            String authors,
            String publisher,
            String searchType,
            String image
    ) {
        this.bookIsbn = bookIsbn;
        this.title = title;
        this.cover = cover;
        this.authors = authors;
        this.publisher = publisher;
        this.searchType = searchType;
        this.image = image;
    }

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
    
    // 카카오 API → BookDto 변환 메서드
    public static BookDto fromKakao(com.kosta.readdam.dto.kakao.KakaoBookResponse.Document doc) {
        return BookDto.builder()
                .bookIsbn(doc.getIsbn())
                .title(doc.getTitle())
                .cover(doc.getThumbnail())
                .authors(String.join(", ", doc.getAuthors()))
                .publisher(doc.getPublisher())
                .searchType("BOOK")
                .image(doc.getThumbnail())
                .build();
    }
}
