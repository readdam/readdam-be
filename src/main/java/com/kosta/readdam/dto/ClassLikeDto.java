package com.kosta.readdam.dto;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassLike;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassLikeDto {

    private Integer likeId;
    private String username;
    private Integer classId;
    private LocalDateTime date;

    public ClassLike toEntity(User user, ClassEntity classEntity) {
        return ClassLike.builder()
                .likeId(likeId)
                .user(user)
                .classId(classEntity)
                .date(date != null ? date : LocalDateTime.now())
                .build();
    }
}
