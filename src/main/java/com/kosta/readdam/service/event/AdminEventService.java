package com.kosta.readdam.service.event;

import java.util.List;

import com.kosta.readdam.dto.EventDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.dto.WriteShortDto;

public interface AdminEventService {

	EventDto createEvent(EventDto dto);

	PagedResponse<EventDto> getEvents(int page, int size);

	List<EventDto> getAllEvents();

	EventDto getEventDetails(Integer eventId);

	List<WriteShortDto> getAllByEvent(Integer eventId);

	List<WriteShortDto> getTop3ByEvent(Integer eventId);

	List<EventDto> getUpcomingEvents();

	List<EventDto> getOngoingEvents();

	List<EventDto> getCompletedEvents();

	void distributePoints(Integer eventId);

	void deleteEvent(Integer eventId);
	
	List<String> getYearMonths();

	EventDto getCurrentEvent();
	
	List<WriteShortDto> getWriteShorts(Integer eventId, String yearMonth, String sortBy);
	
	PagedResponse<WriteShortDto> getParticipationsByMonth(String yearMonth, String sortBy, int page, int size);

}
