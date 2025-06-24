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
}
