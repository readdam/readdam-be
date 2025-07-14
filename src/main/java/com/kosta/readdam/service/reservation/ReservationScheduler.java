package com.kosta.readdam.service.reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.enums.ClassStatus;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.klass.ClassRepository;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ClassRepository       classRepository;

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void checkPendingReservations() {
        LocalDateTime now = LocalDateTime.now();

        // PENDING 예약 전체 조회
        List<Reservation> list = reservationRepository.findByStatus(ReservationStatus.PENDING);

        for (Reservation r : list) {
            // ① 예약 생성 1주일 후 시점
            LocalDateTime weekLater = r.getCreatedAt().plusWeeks(1);

            // ② 세부 예약 중 가장 이른 시작 시각
            LocalDateTime firstSession = r.getDetails().stream()
                .map(d -> d.getDate().atTime(d.getTime()))
                .min(LocalDateTime::compareTo)
                .orElse(weekLater); // 세션 정보 없으면 일주일 후만 고려

            // “일주일 지났거나, 첫 수업 시작 시간이 지났으면” 처리
            if (now.isBefore(weekLater) && now.isBefore(firstSession)) {
                continue; // 아직 처리 시점 아님
            }

            ClassEntity c = r.getClassEntity();
            if (c == null) {
                r.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(r);
            } else {
                long joined = c.getClassUsers().stream()
                               .filter(u -> u.getLeftDate() == null)
                               .count();

                if (joined >= c.getMinPerson()) {
                    c.setStatus(ClassStatus.CONFIRMED);
                    classRepository.save(c);
                } else {
                    c.setStatus(ClassStatus.CANCELLED);
                    r.setStatus(ReservationStatus.CANCELLED);
                    classRepository.save(c);
                    reservationRepository.save(r);
                }
            }
        }
    }
}



