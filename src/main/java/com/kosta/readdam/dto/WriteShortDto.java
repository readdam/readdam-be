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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteShortDto {

    private Integer writeshortId;
    private String content;
    private String color;
    private String username;
    private String nickname;
    private Integer eventId;
    private LocalDateTime regDate;
    private Boolean isHide;
    
    private String eventTitle;
    private Long likeCount;

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
    

    public WriteShortDto(String eventTitle, String content, long likeCount) {
        this.eventTitle = eventTitle;
        this.content = content;
        this.likeCount = likeCount;
    }
    
    public static WriteShortDto from(WriteShort entity) {
        return WriteShortDto.builder()
                .writeshortId(entity.getWriteshortId())
                .content(entity.getContent())
                .color(entity.getColor())
                .username(entity.getUser().getUsername())  // User 엔터티에서 이름 추출
                .nickname(entity.getUser().getNickname()) 
                .eventId(entity.getEvent().getEventId())  // Event 엔터티에서 ID 추출
                .regDate(entity.getRegDate())
                .isHide(entity.getIsHide())
                .build();

    }
}
