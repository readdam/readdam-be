package com.kosta.readdam.entity;

import com.kosta.readdam.dto.BookReviewDto;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_review_id", nullable = false, updatable = false)
    private Integer bookReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "reg_time", nullable = false)
    private LocalDateTime regTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;

    public BookReviewDto toDto() {
        return BookReviewDto.builder()
                .bookReviewId(bookReviewId)
                .bookIsbn(book.getBookIsbn())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .rating(rating)
                .comment(comment)
                .regTime(regTime)
                .isHide(isHide)
                .build();
    }
}
