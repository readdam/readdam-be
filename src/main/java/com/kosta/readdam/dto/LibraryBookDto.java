package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Library;
import com.kosta.readdam.entity.LibraryBook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBookDto {
    private Integer librarybookId;
    private String bookName;
    private String bookWriter;
    private String bookImg;
    private Integer libraryId;
    
    public LibraryBook toEntity(Library library) {
        return LibraryBook.builder()
                .librarybookId(this.librarybookId)
                .bookName(this.bookName)
                .bookWriter(this.bookWriter)
                .bookImg(this.bookImg)
                .library(library)
                .build();
    }
}
