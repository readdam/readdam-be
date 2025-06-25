package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.Event;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WriteShortDto {
    private Integer writeshortId;
    private String content;
    private String color;
    private String username;
    private String nickname;
    private Integer eventId;
    private String eventTitle;     
    private LocalDateTime regDate;
    private Boolean isHide;

    private Integer likes;         
    private Boolean isLiked;       
    private Long likeCount;        

    // 엔티티 → DTO (비로그인용)
    public static WriteShortDto from(WriteShort entity) {
        return WriteShortDto.builder()
            .writeshortId(entity.getWriteshortId())
            .content(entity.getContent())
            .color(entity.getColor())
            .username(entity.getUser().getUsername())
            .nickname(entity.getUser().getNickname())
            .eventId(entity.getEvent().getEventId())
            .eventTitle(entity.getEvent().getTitle())   
            .regDate(entity.getRegDate())
            .isHide(entity.getIsHide())
            .likes(entity.getLikes())
            .likeCount(entity.getLikes().longValue())   
            .isLiked(false)
            .build();
    }

    // 엔티티 + 로그인 유저 좋아요 여부용
    public static WriteShortDto from(WriteShort entity, boolean isLiked, int likes) {
        return WriteShortDto.builder()
            .writeshortId(entity.getWriteshortId())
            .content(entity.getContent())
            .color(entity.getColor())
            .username(entity.getUser().getUsername())
            .nickname(entity.getUser().getNickname())
            .eventId(entity.getEvent().getEventId())
            .eventTitle(entity.getEvent().getTitle())   
            .regDate(entity.getRegDate())
            .isHide(entity.getIsHide())
            .likes(likes)
            .likeCount((long) likes)                   
            .isLiked(isLiked)
            .build();
    }
    
    public WriteShort toEntity(User user, Event event) {
        return WriteShort.builder()
                .writeshortId(writeshortId)
                .content(content)
                .color(color)
                .user(user)
                .event(event)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .isHide(isHide != null ? isHide : false)
                .build();
    }
}

