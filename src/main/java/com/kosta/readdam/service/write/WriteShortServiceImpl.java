package com.kosta.readdam.service.write;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.Event;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.repository.EventRepository;
import com.kosta.readdam.repository.WriteShortRepository;
import com.kosta.readdam.util.PageInfo2;
@Service
public class WriteShortServiceImpl implements WriteShortService {
    @Autowired
    private WriteShortRepository writeShortRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventService eventService;
	
	@Override
	public WriteShortDto writePostcard(WriteShortDto writeShortDto, User user) {
		// 존재하는 Event인지 확인
		Event event = eventService.findCurrentEventEntity(LocalDateTime.now()); 
	    // DTO의 toEntity() 메서드로 엔티티 생성 후 저장
	    WriteShort saved = writeShortRepository.save(writeShortDto.toEntity(user, event));
	 // 저장된 엔티티를 DTO로 변환하여 반환
	    return WriteShortDto.from(saved);
	}


	@Override
	public Map<String, Object> getWriteShortListByCurrentEvent(int page, int size) {
	    Event event = eventService.findCurrentEventEntity(LocalDateTime.now());

	    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("regDate").descending());

	    Page<WriteShort> pageResult = writeShortRepository
	            .findByEvent_EventIdAndIsHideFalse(event.getEventId(), pageable);

	    List<WriteShortDto> list = pageResult.getContent().stream()
	            .map(WriteShortDto::from)
	            .collect(Collectors.toList());

	    PageInfo2 pageInfo = new PageInfo2(
	            page,
	            size,
	            false,
	            (int) pageResult.getTotalElements(),
	            pageResult.getTotalPages()
	    );

	    Map<String, Object> map = new HashMap<>();
	    map.put("list", list);
	    map.put("pageInfo", pageInfo);

	    return map;
	}

}
