package com.kosta.readdam.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.reservation.ReservationCreateRequest;
import com.kosta.readdam.dto.reservation.ReservationDetailListDto;
import com.kosta.readdam.dto.reservation.ReservationTimeRange;
import com.kosta.readdam.dto.reservation.ReservationTimeResponse;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.kosta.readdam.repository.ReservationDetailRepository;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.klass.ClassUserRepository;
import com.kosta.readdam.repository.place.PlaceRoomRepository;
import com.kosta.readdam.repository.place.PlaceTimeRepository;
import com.kosta.readdam.repository.reservation.ReservationDslRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
	private final PlaceTimeRepository placeTimeRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final PlaceRoomRepository placeRoomRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationDslRepositoryImpl reservationDslRepository;
    private final ClassUserRepository classUserRepository;

    public ReservationTimeResponse getAvailableTimes(Integer placeRoomId, LocalDate date) {
        // 요일 체크
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

        // 전체 시간표
        List<PlaceTime> placeTimes = placeTimeRepository.findByPlaceRoom_PlaceRoomIdAndActiveAndIsWeekend(
                placeRoomId,
                true,
                isWeekend
        );

        List<String> allTimes = placeTimes.stream()
                .map(PlaceTime::getTime)
                .collect(Collectors.toList());

        // 예약된 시간
        List<LocalTime> reservedLocalTimes = reservationDetailRepository.findReservedTimesByPlaceRoomIdAndDate(placeRoomId, date);
        List<String> reservedTimes = reservedLocalTimes.stream()
                .map(LocalTime::toString)
                .collect(Collectors.toList());

        // 예약 가능한 시간 = 전체 - 예약된
        List<String> availableTimes = allTimes.stream()
                .filter(t -> !reservedTimes.contains(t))
                .collect(Collectors.toList());

        return new ReservationTimeResponse(allTimes, reservedTimes, availableTimes);
    }
    
    @Override
    public void createReservations(String username, List<ReservationCreateRequest> requests) {
        // User Entity 가져오기
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid username"));

        for (ReservationCreateRequest request : requests) {
            // FK로 연결할 PlaceRoom Entity 가져오기
            PlaceRoom placeRoom = placeRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid roomId: " + request.getRoomId()));

            // 날짜별로 그룹핑
            Map<String, List<String>> dateToTimesMap = new LinkedHashMap<>();
            for (ReservationTimeRange range : request.getRanges()) {
                dateToTimesMap.computeIfAbsent(range.getDate(), k -> new ArrayList<>()).addAll(range.getTimes());
            }

            // 날짜마다 개별 Reservation 생성
            for (Map.Entry<String, List<String>> entry : dateToTimesMap.entrySet()) {
                String dateStr = entry.getKey();
                List<String> timeStrs = entry.getValue();

                // Reservation Entity 생성
                Reservation reservation = new Reservation();
                reservation.setPlaceRoom(placeRoom);
                reservation.setUser(user);
                reservation.setParticipantCount(request.getParticipantCount());
                reservation.setRequestMessage(request.getRequestMessage());
                reservation.setReserverName(request.getReserverName());
                reservation.setReserverPhone(request.getReserverPhone());
                reservation.setStatus(ReservationStatus.PENDING);

                reservationRepository.save(reservation);

                // ReservationDetail 생성
                LocalDate date = LocalDate.parse(dateStr);
                for (String timeStr : timeStrs) {
                    LocalTime time = LocalTime.parse(timeStr);

                    ReservationDetail detail = new ReservationDetail();
                    detail.setReservation(reservation);
                    detail.setDate(date);
                    detail.setTime(time);

                    reservationDetailRepository.save(detail);
                }
            }
        }
    }
    
    @Override
    public Page<ReservationDetailListDto> getReservationPage(Pageable pageable, String date, String status, String keyword) {
        return reservationDslRepository.findReservations(pageable, date, status, keyword);
    }
    
    @Override
    @Transactional
    public void cancelReservation(Integer reservationId, String username) {
        Reservation r = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

        // 예약자 본인 확인
        if (!r.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 예약만 취소할 수 있습니다.");
        }

        // 연결된 강의가 있는지
        ClassEntity c = r.getClassEntity();
        if (c == null) {
            // 강의 미생성 → 바로 취소
            r.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(r);
            return;
        }

        // 활성(퇴장일 없는) 참여자 수 조회
        long activeCount = classUserRepository.countByClassEntityAndLeftDateIsNull(c);
        // 나 혼자(=1명이면) 취소 가능
        if (activeCount > 1) {
            throw new IllegalStateException("다른 참여자가 있어 예약을 취소할 수 없습니다.");
        }

        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
    }

}
