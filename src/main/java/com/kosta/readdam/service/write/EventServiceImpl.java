package com.kosta.readdam.service.write;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.EventDto;
import com.kosta.readdam.entity.Event;
import com.kosta.readdam.repository.EventRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

	@Override
	public EventDto findCurrentEvent(LocalDateTime now) {
        Event event = eventRepository.findCurrentEvent(now)
                .orElseThrow(() -> new IllegalStateException("진행 중인 이벤트가 없습니다."));
            return EventDto.from(event);
	}

	@Override
	public Event findCurrentEventEntity(LocalDateTime now) {
        return eventRepository.findCurrentEvent(now)
                .orElseThrow(() -> new IllegalStateException("진행 중인 이벤트가 없습니다."));
        }
	}
	



