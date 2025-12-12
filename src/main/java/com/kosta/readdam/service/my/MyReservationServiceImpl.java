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
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.kosta.readdam.repository.ReservationDetailRepository;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.klass.ClassUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyReservationServiceImpl implements MyReservationService {
    private final ReservationDetailRepository detailRepo;
    private final ReservationRepository reservationRepo;
    private final ClassUserRepository classUserRepository;

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

        // 본인 예약 검증
        if (!r.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 예약만 취소할 수 있습니다.");
        }

        // 강의 생성 여부 확인
        // ClassEntity 는 Reservation에 @OneToOne(mappedBy="reservation") 로 매핑되어 있어야 합니다.
        ClassEntity c = r.getClassEntity();

        if (c != null) {
            // 1) 강의 생성 후: 본인 제외 다른 참여자 유무 체크
            long others = classUserRepository.countByClassEntityAndLeftDateIsNull(c);
            if (others > 0) {
                throw new IllegalStateException("다른 참여자가 있어 예약을 취소할 수 없습니다.");
            }
        }

        // 2) 당일 예약 취소 불가 (강의 생성 후 또는 생성 전 상관없이 원하시면 c != null 조건 추가)
        boolean sameDay = r.getDetails().stream()
            .anyMatch(d -> d.getDate().isEqual(LocalDate.now()));
        if (sameDay) {
            throw new IllegalStateException("당일 예약은 취소할 수 없습니다.");
        }

        // 3) 예약 상세 삭제 및 상태 변경
        detailRepo.deleteAllByReservation_ReservationId(reservationId);
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
