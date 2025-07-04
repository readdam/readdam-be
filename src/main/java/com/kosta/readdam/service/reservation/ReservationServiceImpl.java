package com.kosta.readdam.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.reservation.ReservationCreateRequest;
import com.kosta.readdam.dto.reservation.ReservationTimeRange;
import com.kosta.readdam.dto.reservation.ReservationTimeResponse;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.kosta.readdam.repository.ReservationDetailRepository;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.place.PlaceRoomRepository;
import com.kosta.readdam.repository.place.PlaceTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
	private final PlaceTimeRepository placeTimeRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final PlaceRoomRepository placeRoomRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

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

            // ReservationDetail Entity 생성
            for (ReservationTimeRange range : request.getRanges()) {
                LocalDate date = LocalDate.parse(range.getDate());
                for (String timeStr : range.getTimes()) {
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
}
