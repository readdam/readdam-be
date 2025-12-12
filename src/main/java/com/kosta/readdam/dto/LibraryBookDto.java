package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Library;
import com.kosta.readdam.entity.LibraryBook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBookDto {
    private String isbn;
    private String title;
    private String thumbnail;
    private String publisher;
    private String datetime;
    private String[] authors;

    public LibraryBook toEntity(Library library) {
        return LibraryBook.builder()
                .library(library)
                .isbn(this.isbn)
                .title(this.title)
                .thumbnail(this.thumbnail)
                .publisher(this.publisher)
                .datetime(this.datetime)
                .authors(this.authors != null ? String.join(", ", this.authors) : null)
                .build();
    }
}
