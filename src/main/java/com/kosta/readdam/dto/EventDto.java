package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Event;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    private Integer eventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Event toEntity() {
        return Event.builder()
                .eventId(eventId)
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
