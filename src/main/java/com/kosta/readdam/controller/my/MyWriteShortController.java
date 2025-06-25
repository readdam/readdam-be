package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.service.my.MyWriteShortService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@Slf4j
public class MyWriteShortController {

    private final MyWriteShortService myWriteShortService;

    /** 내 단문 목록 조회 */
    @GetMapping("/myWriteShort")
    public ResponseEntity<?> getMyWriteShorts(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        try {
            List<WriteShortDto> list = myWriteShortService.getMyWriteShorts(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("단문 목록 조회 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("단문 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /** 단문 좋아요 토글 */
    @PostMapping("/myLikeShort/{id}")
    public ResponseEntity<?> toggleLike(
            @PathVariable("id") Integer writeshortId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        try {
            WriteShortDto dto = myWriteShortService.toggleLike(writeshortId, username);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("단문 좋아요 토글 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("단문 좋아요 토글 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
