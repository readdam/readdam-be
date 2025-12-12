package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Book;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLikeDto {

    private Integer likeId;
    private String username;
    private String bookIsbn;
    private LocalDateTime date;

    public BookLike toEntity(User user, Book book) {
        return BookLike.builder()
                .likeId(likeId)
                .user(user)
                .book(book)
                .date(date)
                .build();
    }
}
