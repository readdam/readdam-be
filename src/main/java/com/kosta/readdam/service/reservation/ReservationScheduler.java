package com.kosta.readdam.service.reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.enums.ClassStatus;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.klass.ClassRepository;
import com.kosta.readdam.repository.klass.ClassUserRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationScheduler.class);

    private final ReservationRepository reservationRepository;
    private final ClassRepository       classRepository;
    private final ClassUserRepository   classUserRepository;  // 추가

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkPendingReservations() {
        LocalDateTime now = LocalDateTime.now();
        log.info("▶ checkPendingReservations 실행: {}", now);

        List<Reservation> list = reservationRepository.findByStatus(ReservationStatus.PENDING);
        log.info("   PENDING 예약 개수: {}", list.size());

        for (Reservation r : list) {
            LocalDateTime weekLater = r.getCreatedAt().plusWeeks(1);
            LocalDateTime firstSession = r.getDetails().stream()
                .map(d -> d.getDate().atTime(d.getTime()))
                .min(LocalDateTime::compareTo)
                .orElse(weekLater);

            if (now.isBefore(weekLater) && now.isBefore(firstSession)) {
                continue;
            }

            ClassEntity c = r.getClassEntity();
            if (c == null) {
                r.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(r);
                log.info("   예약 ID {}: 클래스 없음 → 취소 처리", r.getReservationId());
            } else {
                long joined = classUserRepository
                    .countByClassEntityAndLeftDateIsNull(c);

                if (joined >= c.getMinPerson()) {
                    c.setStatus(ClassStatus.CONFIRMED);
                    classRepository.save(c);
                    log.info("   예약 ID {}: 참여 {}명 ≥ 최소 {}명 → CONFIRMED",
                             r.getReservationId(), joined, c.getMinPerson());
                } else {
                    c.setStatus(ClassStatus.CANCELLED);
                    r.setStatus(ReservationStatus.CANCELLED);
                    classRepository.save(c);
                    reservationRepository.save(r);
                    log.info("   예약 ID {}: 참여 {}명 < 최소 {}명 → 모두 취소",
                             r.getReservationId(), joined, c.getMinPerson());
                }
            }
        }
    }
}
