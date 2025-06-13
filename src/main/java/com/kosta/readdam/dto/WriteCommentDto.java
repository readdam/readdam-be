package com.kosta.readdam.dto;

import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.entity.WriteComment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteCommentDto {

    private Integer writeCommentId;
    private String content;
    private Integer writeId;
    private String username;
    private LocalDateTime regDate;
    private Boolean adopted;
    private Boolean isSecret;
    private Boolean isHide;

    public WriteComment toEntity(Write write, User user) {
        return WriteComment.builder()
                .writeCommentId(writeCommentId)
                .content(content)
                .write(write)
                .user(user)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .adopted(adopted != null ? adopted : false)
                .isSecret(isSecret != null ? isSecret : false)
                .isHide(isHide != null ? isHide : false)
                .build();
    }
}
