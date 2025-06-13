package com.kosta.readdam.entity;

import com.kosta.readdam.dto.BookLikeDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false, updatable = false)
    private Integer likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    public BookLikeDto toDto() {
        return BookLikeDto.builder()
                .likeId(likeId)
                .username(user.getUsername())
                .bookIsbn(book.getBookIsbn())
                .date(date)
                .build();
    }
}
