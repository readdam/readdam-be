package com.kosta.readdam.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.kosta.readdam.entity.Event;

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
public class EventDto {

    private Integer eventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean pointsDistributed;
    
    private List<WriteShortDto> topParticipants;

    public Event toEntity() {
        return Event.builder()
                .eventId(eventId)
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .pointsDistributed(pointsDistributed != null ? pointsDistributed : false)
                .build();
    }
    
    public static EventDto from(Event event) {
        return EventDto.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())         
                .startTime(event.getStartTime())   
                .endTime(event.getEndTime())  
                .pointsDistributed(event.getPointsDistributed())
                .build();
    }
    
    public static EventDto from(Event event, List<WriteShortDto> top3) {
        return EventDto.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .topParticipants(top3)
                .pointsDistributed(event.getPointsDistributed())
                .build();
    }
}
