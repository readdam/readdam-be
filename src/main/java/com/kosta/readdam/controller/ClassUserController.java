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
    private final ClassRepository classRepo;

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

    /** 수강 신청 */
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
    
    @GetMapping("/participation-info")
    public ParticipationInfoDto getParticipationInfo(
    		@PathVariable Integer classId,
    	    @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ClassEntity c = classRepo.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));
        // 총 시간
        int totalTime = c.getTotalTime();
        // 포인트 계산
        int usedPoints = 500 * totalTime / c.getMinPerson();
        // 취소 가능 기한 (첫 예약일 + 1주)
        LocalDate created = c.getReservation().getCreatedAt().toLocalDate();
        LocalDate cancelDeadline = created.plusWeeks(1);

        // 현재 로그인 사용자
        String username = principalDetails.getUsername();
        // 이미 참여했는지
        boolean joined = classUserRepository
            .existsByClassEntityAndUser_Username(c, username);
        // 취소 가능 여부: joined && 아직 취소 마감 안 지났으면 true
        boolean canCancel = joined && LocalDate.now().isBefore(cancelDeadline);

        // 현재 참여 인원 (leftDate == null 인 사람만)
        int currentParticipants = (int) classUserRepository
            .countByClassEntity_ClassIdAndLeftDateIsNull(classId);

        return new ParticipationInfoDto(
            usedPoints,
            cancelDeadline,
            joined,
            canCancel,
            currentParticipants
        );
    }
}
