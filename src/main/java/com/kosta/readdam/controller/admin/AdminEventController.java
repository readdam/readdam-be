package com.kosta.readdam.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.EventDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.service.event.AdminEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService adminEventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto dto) {
        EventDto created = adminEventService.createEvent(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EventDto>> getPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<EventDto> resp = adminEventService.getEvents(page, size);
        return ResponseEntity.ok(resp);
    }

    /** 전체 이벤트 조회 (페이징 없이) */
    @GetMapping("/all")
    public ResponseEntity<List<EventDto>> getAll() {
        List<EventDto> list = adminEventService.getAllEvents();
        return ResponseEntity.ok(list);
    }

    /** 특정 이벤트 상세 + TOP3 한줄글 */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getDetails(@PathVariable Integer eventId) {
        EventDto dto = adminEventService.getEventDetails(eventId);
        return ResponseEntity.ok(dto);
    }

    /** 해당 이벤트의 모든 한줄글 (최신순) */
    @GetMapping("/{eventId}/writeShorts")
    public ResponseEntity<List<WriteShortDto>> getWriteShorts(
            @PathVariable Integer eventId,
            @RequestParam String yearMonth,
            @RequestParam(defaultValue = "date") String sortBy) {
        List<WriteShortDto> list = adminEventService.getWriteShorts(eventId, yearMonth, sortBy);
        return ResponseEntity.ok(list);
    }

    /** 해당 이벤트의 TOP3 한줄글 (좋아요순) */
    @GetMapping("/{eventId}/writeShorts/top3")
    public ResponseEntity<List<WriteShortDto>> getTop3WriteShorts(@PathVariable Integer eventId) {
        List<WriteShortDto> list = adminEventService.getTop3ByEvent(eventId);
        return ResponseEntity.ok(list);
    }

    /** 다가오는 이벤트 */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDto>> getUpcoming() {
        return ResponseEntity.ok(adminEventService.getUpcomingEvents());
    }

    /** 진행 중 이벤트 */
    @GetMapping("/ongoing")
    public ResponseEntity<List<EventDto>> getOngoing() {
        return ResponseEntity.ok(adminEventService.getOngoingEvents());
    }

    /** 완료된 이벤트 (TOP3 포함) */
    @GetMapping("/completed")
    public ResponseEntity<List<EventDto>> getCompleted() {
        return ResponseEntity.ok(adminEventService.getCompletedEvents());
    }

    /** 포인트 지급 */
    @PostMapping("/{eventId}/distribute-points")
    public ResponseEntity<Void> distributePoints(@PathVariable Integer eventId) {
        adminEventService.distributePoints(eventId);
        return ResponseEntity.ok().build();
    }

    /** 이벤트 삭제 */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        adminEventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/year-months")
    public ResponseEntity<List<String>> getYearMonths() {
        List<String> yearMonths = adminEventService.getYearMonths();
        return ResponseEntity.ok(yearMonths);
    }

    @GetMapping("/current")
    public ResponseEntity<EventDto> getCurrentEvent() {
        EventDto dto = adminEventService.getCurrentEvent();
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/participations")
    public ResponseEntity<PagedResponse<WriteShortDto>> getByMonth(
    	    @RequestParam String yearMonth,
    	    @RequestParam(defaultValue = "date") String sortBy,
    	    @RequestParam(defaultValue = "1") int page,
    	    @RequestParam(defaultValue = "20") int size
    	) {
    	    PagedResponse<WriteShortDto> resp =
    	        adminEventService.getParticipationsByMonth(yearMonth, sortBy, page, size);
    	    return ResponseEntity.ok(resp);
    	}

}
