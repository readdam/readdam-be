package com.kosta.readdam.controller.write;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.EventDto;
import com.kosta.readdam.service.write.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    
    // 현재 진행 중인 이벤트 조회 (프론트에서 호출)
    @GetMapping("/event")
    public ResponseEntity<EventDto> getCurrentEvent() {
        EventDto eventDto = eventService.findCurrentEvent(LocalDateTime.now());
        return ResponseEntity.ok(eventDto);
    }
}
