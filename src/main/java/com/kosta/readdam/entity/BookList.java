package com.kosta.readdam.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kosta.readdam.dto.BookListDto;
import com.kosta.readdam.entity.enums.BookListCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookList {

    @Id
    @Column(length =  255)
    private String id;

    @Column(nullable = false, length = 20)
    private String isbn;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookListCategory category;
    
    public BookListDto toDto(Integer reviewCnt, BigDecimal rating) {
        return BookListDto.builder()
                .id(this.id)
                .isbn(this.isbn)
                .imageName(this.imageName)
                .author(this.author)
                .publisher(this.publisher)
                .reviewCnt(reviewCnt)
                .rating(rating)
                .build();
    }
}


