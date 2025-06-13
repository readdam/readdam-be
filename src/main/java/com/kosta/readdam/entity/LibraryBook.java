package com.kosta.readdam.entity;

import javax.persistence.*;

import com.kosta.readdam.dto.LibraryBookDto;

import lombok.*;

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

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "book_writer", nullable = false)
    private String bookWriter;

    @Column(name = "book_img")
    private String bookImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    public LibraryBookDto toDto() {
        return LibraryBookDto.builder()
                .librarybookId(this.librarybookId)
                .bookName(this.bookName)
                .bookWriter(this.bookWriter)
                .bookImg(this.bookImg)
                .libraryId(this.library.getLibraryId())
                .build();
    }
}
