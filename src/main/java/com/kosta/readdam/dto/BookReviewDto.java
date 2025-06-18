package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookReview;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewDto {

    private Integer bookReviewId;
    private String bookIsbn;
    private String username;
    private String nickname;
    private String profileImg;
    private BigDecimal rating;
    private String comment;
    private LocalDateTime regTime;
    private Boolean isHide;

    public BookReview toEntity(User user, Book book) {
        return BookReview.builder()
                .bookReviewId(bookReviewId)
                .user(user)
                .book(book)
                .rating(rating)
                .comment(comment)
                .regTime(regTime != null ? regTime : LocalDateTime.now())
                .isHide(isHide != null ? isHide : false)
                .build();
    }
    
    public static BookReviewDto fromEntity(BookReview r) {
        return BookReviewDto.builder()
                .bookReviewId(r.getBookReviewId())
                .bookIsbn(r.getBook().getBookIsbn())
                .username(r.getUser().getUsername())
                .nickname(r.getUser().getNickname())
                .profileImg(r.getUser().getProfileImg())
                .rating(r.getRating())
                .comment(r.getComment())
                .regTime(r.getRegTime())
                .isHide(r.getIsHide())
                .build();
    }

}
