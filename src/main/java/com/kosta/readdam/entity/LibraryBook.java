package com.kosta.readdam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kosta.readdam.dto.LibraryBookDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "library_book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "librarybook_id", nullable = false, updatable = false)
    private Integer librarybookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "authors")
    private String authors; // 여러 명일 경우 ", "로 구분

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "datetime")
    private String datetime;

    public LibraryBookDto toDto() {
        return LibraryBookDto.builder()
                .isbn(this.isbn)
                .title(this.title)
                .authors(this.authors != null ? this.authors.split(",\\s*") : null)
                .thumbnail(this.thumbnail)
                .publisher(this.publisher)
                .datetime(this.datetime)
                .build();
    }
}
