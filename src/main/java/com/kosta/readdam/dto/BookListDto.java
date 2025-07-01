package com.kosta.readdam.dto;



import java.math.BigDecimal;

import com.kosta.readdam.entity.BookList;
import com.kosta.readdam.entity.enums.BookListCategory;

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
public class BookListDto {

	private String id;
	private String isbn;
	private String imageName;
	private String author;
	private String publisher;
	private BookListCategory category;   
	private int reviewCnt;               
	private BigDecimal rating;
    
    public BookList toEntity() {
        return BookList.builder()
                .id(this.id)
                .isbn(this.isbn)
                .imageName(this.imageName)
                .author(this.author)
                .publisher(this.publisher)
                .category(this.category)
                .build();
    }
}
