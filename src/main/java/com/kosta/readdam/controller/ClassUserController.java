package com.kosta.readdam.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.ParticipationInfoDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.klass.ClassRepository;
import com.kosta.readdam.repository.klass.ClassUserRepository;
import com.kosta.readdam.service.klass.ClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classDetail/{classId}")
public class ClassUserController {

    private final ClassService           classService;
    private final ClassUserRepository    classUserRepository;
    private final ClassRepository        classRepo;
    private final ReservationRepository  reservationRepo;
    private final UserRepository userRepository;

    /** 참여 상태 조회 */
    @GetMapping("/join-status")
    public ResponseEntity<Map<String, Boolean>> getJoinStatus(
            Principal principal,
            @PathVariable Integer classId
    ) {
        String username = principal != null ? principal.getName() : null;
        boolean joined = false;
        if (username != null) {
            joined = classUserRepository
                .existsByClassEntity_ClassIdAndUser_Username(classId, username);
        }
        Map<String, Boolean> result = new HashMap<>();
        result.put("joined", joined);
        return ResponseEntity.ok(result);
    }

    /** 수강 신청 (placeRoomId 제거) */
    @PostMapping("/join")
    public ResponseEntity<Void> joinClass(
            Principal principal,
            @PathVariable Integer classId
    ) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        }
        classService.joinClass(classId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /** 수강 취소 */
    @DeleteMapping("/join")
    public ResponseEntity<Void> cancelJoinClass(
            Principal principal,
            @PathVariable Integer classId
    ) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        }
        classService.cancelJoinClass(classId, principal.getName());
        return ResponseEntity.ok().build();
    }
    
    /** 참여 정보 조회 */
    @GetMapping("/participation-info")
    public ParticipationInfoDto getParticipationInfo(
            @PathVariable Integer classId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        }

        ClassEntity c = classRepo.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));

        int detailCount = reservationRepo
                .findByClassEntity_ClassId(classId)                // 해당 클래스의 모든 Reservation
                .stream()
                .mapToInt(res -> res.getDetails().size())         // 각 예약의 detail 수 합산
                .sum();

            // ② 포인트 계산: 500 * detailCount / minPerson
            int usedPoints = detailCount > 0
                ? 500 * detailCount / c.getMinPerson()
                : 0;

        // ② 첫 예약일 계산 (없으면 오늘 기준)
        LocalDate createdDate = reservationRepo
            .findFirstByClassEntityOrderByCreatedAtAsc(c)
            .map(res -> res.getCreatedAt().toLocalDate())
            .orElse(LocalDate.now());
        LocalDate cancelDeadline = createdDate.plusWeeks(1);

        // ③ 사용자 참여 여부 & 취소 가능 여부
        String username = principalDetails.getUsername();
        boolean joined = classUserRepository
            .existsByClassEntityAndUser_Username(c, username);
        boolean canCancel = joined
            && reservationRepo.findFirstByClassEntityOrderByCreatedAtAsc(c).isPresent()
            && LocalDate.now().isBefore(cancelDeadline);

        // ④ 현재 참여자 수
        int currentParticipants = (int) classUserRepository
            .countByClassEntity_ClassIdAndLeftDateIsNull(classId);
        
     // ③ 사용자 참여 여부 & 취소 가능 여부 앞에
        User u = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. username=" + username));
        int totalPoint = u.getTotalPoint() != null ? u.getTotalPoint() : 0;

        return new ParticipationInfoDto(
        	totalPoint,
            usedPoints,
            cancelDeadline,
            joined,
            canCancel,
            currentParticipants
        );
    }

}
