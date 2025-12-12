package com.kosta.readdam.controller.my;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.ReservationResponseDto;
import com.kosta.readdam.service.my.MyReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyReservationController {

    private final MyReservationService svc;

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(
            @AuthenticationPrincipal PrincipalDetails user) {
        try {
            List<ReservationResponseDto> list = svc.getReservations(user.getUsername());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            // 에러 시 빈 리스트 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Collections.emptyList());
        }
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<String> deleteMyReservation(
            @AuthenticationPrincipal PrincipalDetails user,
            @PathVariable Integer id) {
        try {
            svc.cancelReservation(user.getUsername(), id);
            return ResponseEntity.ok("예약이 취소되었습니다.");
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ise.getMessage());
        } catch (AccessDeniedException ade) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ade.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("예약 취소 실패: " + e.getMessage());
        }
    }
}
