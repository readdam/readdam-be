package com.kosta.readdam.dto;

import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.entity.WriteLike;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteLikeDto {

    private Integer likeId;
    private String username;
    private Integer writeId;
    private LocalDateTime date;

    public WriteLike toEntity(User user, Write write) {
        return WriteLike.builder()
                .likeId(likeId)
                .user(user)
                .write(write)
                .date(date != null ? date : LocalDateTime.now())
                .build();
    }
}
