package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Event;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import lombok.*;

import java.time.LocalDateTime;

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
