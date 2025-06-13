package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassQna;
import com.kosta.readdam.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassQnaDto {

    private Integer classQnaId;
    private Integer classId;
    private String username;
    private String content;
    private String answer;
    private LocalDateTime regDate;
    private Boolean isSecret;
    private Boolean isHide;

    public ClassQna toEntity(User user, ClassEntity classEntity) {
        return ClassQna.builder()
                .classQnaId(classQnaId)
                .user(user)
                .classEntity(classEntity)
                .content(content)
                .answer(answer)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .isSecret(isSecret != null ? isSecret : false)
                .isHide(isHide != null ? isHide : false)
                .build();
    }
}
