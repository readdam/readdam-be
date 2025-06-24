package com.kosta.readdam.service.write;

import java.time.LocalDateTime;

import com.kosta.readdam.dto.EventDto;
import com.kosta.readdam.entity.Event;

public interface EventService {
    EventDto findCurrentEvent(LocalDateTime now);            // 프론트용 (Dto)
    Event findCurrentEventEntity(LocalDateTime now);         // 저장용 (Entity)
}
