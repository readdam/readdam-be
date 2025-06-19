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
    private String nickname;

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
    
    public static WriteCommentDto from(WriteComment comment) {
        return WriteCommentDto.builder()
            .writeCommentId(comment.getWriteCommentId())
            .content(comment.getContent())
            .writeId(comment.getWrite().getWriteId())
            .username(comment.getUser().getUsername())
            .nickname(comment.getUser().getNickname())
            .regDate(comment.getRegDate())
            .adopted(comment.getAdopted())
            .isSecret(comment.getIsSecret())
            .isHide(comment.getIsHide())
            .build();
    }
}
