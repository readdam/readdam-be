package com.kosta.readdam.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.reservation.ReservationTimeResponse;
import com.kosta.readdam.entity.PlaceTime;
import com.kosta.readdam.repository.ReservationDetailRepository;
import com.kosta.readdam.repository.place.PlaceTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
	private final PlaceTimeRepository placeTimeRepository;
    private final ReservationDetailRepository reservationDetailRepository;

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
}
