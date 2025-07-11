package com.kosta.readdam.service.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.EventDto;
import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.Event;
import com.kosta.readdam.entity.Point;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.EventRepository;
import com.kosta.readdam.repository.PointRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteShortRepository;
import com.kosta.readdam.service.NoticeService;
import com.kosta.readdam.service.alert.NotificationService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

	private final EventRepository eventRepository;
	private final WriteShortRepository writeShortRepository;
	private final PointRepository pointRepository;
	private final UserRepository userRepository;
	private final AlertRepository alertRepository;
	private final NotificationService notificationService;
	private final AdminNoticeService noticeService;

	@Override
	@Transactional
	public EventDto createEvent(EventDto dto) {
		Event saved = eventRepository.save(dto.toEntity());
		return EventDto.from(saved);
	}

	@Override
	public PagedResponse<EventDto> getEvents(int page, int size) {
		PageRequest pr = PageRequest.of(page - 1, size);
		Page<Event> pg = eventRepository.findAll(pr);
		List<EventDto> dtos = pg.getContent().stream().map(EventDto::from).collect(Collectors.toList());
		PageInfo2 pageInfo = PageInfo2.from(pg);
		return new PagedResponse<>(dtos, pageInfo);
	}

	@Override
	public List<EventDto> getAllEvents() {
		return eventRepository.findAll().stream().map(EventDto::from).collect(Collectors.toList());
	}

	@Override
	public EventDto getEventDetails(Integer eventId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 ID: " + eventId));
		List<WriteShortDto> top3 = fetchTop3(eventId);
		return EventDto.from(event, top3);
	}

	@Override
	public List<WriteShortDto> getAllByEvent(Integer eventId) {
		return writeShortRepository.findByEventEventIdOrderByRegDateDesc(eventId).stream().map(WriteShortDto::from)
				.collect(Collectors.toList());
	}

	@Override
	public List<WriteShortDto> getTop3ByEvent(Integer eventId) {
		return fetchTop3(eventId);
	}

	@Override
	public List<EventDto> getUpcomingEvents() {
		LocalDateTime now = LocalDateTime.now();
		return eventRepository.findByStartTimeAfter(now).stream().map(e -> {

			List<WriteShortDto> top3 = fetchTop3(e.getEventId());
			return EventDto.from(e, top3);
		}).collect(Collectors.toList());
	}

	@Override
	public List<EventDto> getOngoingEvents() {
		LocalDateTime now = LocalDateTime.now();
		return eventRepository.findOngoingEvents(now).stream().map(e -> {
			// 진행 중 이벤트에도 TOP3 포함
			List<WriteShortDto> top3 = fetchTop3(e.getEventId());
			return EventDto.from(e, top3);
		}).collect(Collectors.toList());
	}

	@Override
	public List<EventDto> getCompletedEvents() {
		LocalDateTime now = LocalDateTime.now();
		return eventRepository.findByEndTimeBefore(now).stream().map(e -> {
			List<WriteShortDto> top3 = fetchTop3(e.getEventId());
			return EventDto.from(e, top3);
		}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void distributePoints(Integer eventId) {
	    Event event = eventRepository.findById(eventId)
	        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 ID: " + eventId));

	    if (Boolean.TRUE.equals(event.getPointsDistributed())) {
	        throw new IllegalStateException("이미 포인트를 지급한 이벤트입니다: " + eventId);
	    }

	    User system = userRepository.findByUsername("system")
	        .orElseThrow(() -> new IllegalStateException("시스템 사용자(system)가 없습니다."));

	    // 지급 플래그 세팅
	    event.setPointsDistributed(true);
	    eventRepository.save(event);

	    // TOP3 글과 수상자 목록
	    List<WriteShort> top3 = writeShortRepository
	        .findTop3ByEvent_EventIdAndIsHideFalseOrderByLikesDesc(eventId);
	    List<String> winnerNames = top3.stream()
	        .map(ws -> ws.getUser().getNickname())
	        .collect(Collectors.toList());

	    String month = event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));

	    // 1) 포인트 지급, 알림 생성 및 FCM 푸시
	    for (WriteShort ws : top3) {
	        User user = ws.getUser();

	        // 포인트 적립
	        Point p = Point.builder()
	            .user(user)
	            .point(1000)
	            .reason(month + " 읽담 한줄 TOP3")
	            .build();
	        pointRepository.save(p);

	        user.setTotalPoint((user.getTotalPoint() == null ? 0 : user.getTotalPoint()) + 1000);
	        userRepository.save(user);

	        // Alert & FCM
	        String alertTitle   = month + " 읽담 한줄 TOP3 축하드립니다!";
	        String alertContent = "1000포인트가 지급되었습니다.";
	        String linkUrl      = "/myPointList";

	        Alert alert = Alert.builder()
	            .sender(system)
	            .receiver(user)
	            .title(alertTitle)
	            .content(alertContent)
	            .type("point")
	            .linkUrl(linkUrl)
	            .build();
	        alertRepository.save(alert);

	        Map<String, String> data = new HashMap<>();
	        data.put("type", "point");
	        data.put("link_url", linkUrl);
	        notificationService.sendPush(
	            user.getUsername(),
	            alertTitle,
	            alertContent,
	            data
	        );
	    }

	    // 2) 공지사항 등록 (수상자 전원 대상 한 번만)
	    String noticeTitle = month + " 읽담 한줄 TOP3 발표 및 축하";
	    String noticeContent = winnerNames.stream()
	        .map(name -> name + "님")
	        .collect(Collectors.joining(", "))
	        + " 축하드립니다! 각 1000포인트가 지급되었습니다.";

	    NoticeDto noticeDto = NoticeDto.builder()
	        .title(noticeTitle)
	        .content(noticeContent)
	        .topFix(false)
	        .build();

	    try {
	        noticeService.createNotice(noticeDto);
	    } catch (Exception e) {
	        // 공지 생성 실패 시 롤백을 원하면 RuntimeException으로 감싸 던집니다.
	        throw new RuntimeException("공지사항 생성에 실패했습니다.", e);
	    }
	}


	@Override
	@Transactional
	public void deleteEvent(Integer eventId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 ID: " + eventId));
		LocalDateTime now = LocalDateTime.now();
		if (event.getStartTime().isAfter(now) || event.getEndTime().isBefore(now)) {
			eventRepository.delete(event);
		} else {
			throw new IllegalStateException("진행 중인 이벤트는 삭제할 수 없습니다: " + eventId);
		}
	}

	private List<WriteShortDto> fetchTop3(Integer eventId) {
		return writeShortRepository.findTop3ByEvent_EventIdAndIsHideFalseOrderByLikesDesc(eventId).stream()
				.map(ws -> WriteShortDto.from(ws, false, ws.getLikes())).collect(Collectors.toList());
	}

	@Override
	public List<String> getYearMonths() {
		return eventRepository.findAll().stream()
				.map(e -> e.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM"))).distinct()
				.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
	}

	@Override
	public EventDto getCurrentEvent() {
		List<Event> ongoing = eventRepository.findOngoingEvents(LocalDateTime.now());
		if (ongoing.isEmpty()) {
			throw new IllegalStateException("현재 진행 중인 이벤트가 없습니다.");
		}
		Event current = ongoing.get(0);
		return EventDto.from(current);
	}

	@Override
	public List<WriteShortDto> getWriteShorts(Integer eventId, String yearMonth, String sortBy) {
		// "2025-06" → 2025-06-01T00:00 ~ 2025-06-30T23:59:59
		LocalDate startDay = YearMonth.parse(yearMonth).atDay(1);
		LocalDateTime start = startDay.atStartOfDay();
		LocalDateTime end = startDay.plusMonths(1).atStartOfDay().minusNanos(1);

		List<WriteShort> list;
		if ("likes".equals(sortBy)) {
			list = writeShortRepository.findByEvent_EventIdAndRegDateBetweenAndIsHideFalseOrderByLikesDesc(eventId,
					start, end);
		} else {
			list = writeShortRepository.findByEvent_EventIdAndRegDateBetweenAndIsHideFalseOrderByRegDateDesc(eventId,
					start, end);
		}

		return list.stream().map(ws -> WriteShortDto.from(ws, false, ws.getLikes())).collect(Collectors.toList());
	}
	
	@Override
	public PagedResponse<WriteShortDto> getParticipationsByMonth(
	    String ym, String sortBy, int page, int size) {

	  YearMonth m = YearMonth.parse(ym);
	  LocalDateTime start = m.atDay(1).atStartOfDay();
	  LocalDateTime end   = m.plusMonths(1).atDay(1).atStartOfDay().minusNanos(1);

	  PageRequest pr = PageRequest.of(page - 1, size,
	    sortBy.equals("likes")
	      ? Sort.by(Sort.Direction.DESC, "likes")
	      : Sort.by(Sort.Direction.DESC, "regDate")
	  );
	  Page<WriteShort> pg = writeShortRepository
	    .findByRegDateBetweenAndIsHideFalse(start, end, pr);

	  List<WriteShortDto> dtos = pg.getContent().stream()
	    .map(ws -> WriteShortDto.from(ws, false, ws.getLikes()))
	    .collect(Collectors.toList());

	  PageInfo2 info = PageInfo2.from(pg);
	  return new PagedResponse<>(dtos, info);
	}


}
