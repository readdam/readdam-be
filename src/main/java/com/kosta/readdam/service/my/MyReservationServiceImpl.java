package com.kosta.readdam.service.my;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.ReservationResponseDto;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.kosta.readdam.repository.ReservationDetailRepository;
import com.kosta.readdam.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyReservationServiceImpl implements MyReservationService {
    private final ReservationDetailRepository detailRepo;
    private final ReservationRepository reservationRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservations(String username) {
        List<ReservationDetail> details =
            detailRepo.findByReservationUserUsername(username);

        Map<Integer, List<ReservationDetail>> grouped =
            details.stream()
                   .collect(Collectors.groupingBy(
                       d -> d.getReservation().getReservationId(),
                       LinkedHashMap::new,
                       Collectors.toList()
                   ));

        List<ReservationResponseDto> result = new ArrayList<>();
        for (List<ReservationDetail> list : grouped.values()) {
            ReservationDetail d0 = list.get(0);
            Reservation r  = d0.getReservation();
            PlaceRoom pr   = r.getPlaceRoom();
            Place p        = pr.getPlace();

            List<String> times = list.stream()
                .map(d -> d.getTime().toString().substring(0,5))
                .sorted()
                .collect(Collectors.toList());
            String start = times.get(0);
            LocalTime tEnd = LocalTime.parse(times.get(times.size() - 1))
                                      .plusHours(1);
            String range = start + " ~ " + tEnd.toString().substring(0,5);

            result.add(ReservationResponseDto.builder()
                .reservationId(r.getReservationId())
                .placeName(pr.getName())
                .basicAddress(p.getBasicAddress())
                .detailAddress(p.getDetailAddress())
                .date(d0.getDate())
                .timeRange(range)
                .placeId(p.getPlaceId())   
                .participantCount(r.getParticipantCount())
                .image(pr.getImg1())
                .status(r.getStatus())
                .build()
            );
        }

        return result;
    }

    @Override
    @Transactional
    public void cancelReservation(String username, Integer reservationId) {
        Reservation r = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("예약이 존재하지 않습니다."));
        if (!r.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 예약만 취소할 수 있습니다.");
        }
        if (r.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("이미 확정된 예약은 취소할 수 없습니다.");
        }
        boolean sameDay = r.getDetails().stream()
            .anyMatch(d -> d.getDate().isEqual(LocalDate.now()));
        if (sameDay) {
            throw new IllegalStateException("당일 예약은 취소할 수 없습니다.");
        }
        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepo.save(r);
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void confirmTodayReservations() {
        LocalDate today = LocalDate.now();
        List<ReservationDetail> details = detailRepo.findByDate(today);
        details.stream()
            .map(ReservationDetail::getReservation)
            .distinct()
            .filter(r -> r.getStatus() == ReservationStatus.PENDING)
            .forEach(r -> r.setStatus(ReservationStatus.CONFIRMED));
    }
}
