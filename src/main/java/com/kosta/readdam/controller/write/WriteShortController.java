package com.kosta.readdam.controller.write;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.dto.WriteShortLikeDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.service.write.EventService;
import com.kosta.readdam.service.write.WriteShortService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class WriteShortController {

	@Autowired
	private WriteShortService writeShortService;
	@Autowired
	private EventService eventService;

	// 읽담한줄 목록 조회 (로그인 필요 없음)
	@GetMapping("/writeShortList")
	public ResponseEntity<Map<String, Object>> getWriteShortList(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
        	@AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
	        // 로그인 사용자의 username 추출 (비로그인 시 null)
	        String username = (principalDetails != null) ? principalDetails.getUsername() : null;
			
			// 현재 시간 기준으로 자동 이벤트 조회 (서비스 내부에서 처리)
			Map<String, Object> res = writeShortService.getWriteShortListByCurrentEvent(page, size, username);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	// 최신 이벤트에 읽담한줄 답변 등록 (로그인 필요)
	@PostMapping("/my/writeShort")
	public ResponseEntity<WriteShortDto> createWriteShort(@RequestBody WriteShortDto writeShortDto,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			User user = principalDetails.getUser(); // jwt 인증 사용자

			// 서비스에서 event 조회 및 저장 로직 모두 처리
			WriteShortDto savedDto = writeShortService.writePostcard(writeShortDto, user);
			return new ResponseEntity<>(savedDto, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		}
	}
	
    @PostMapping("/my/writeShort-like")
    public ResponseEntity<?> toggleWriteShortLike(@RequestBody WriteShortLikeDto writeShortLikeDto,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return new ResponseEntity<>("로그인 필요", HttpStatus.UNAUTHORIZED);
        }

        try {
            String username = principalDetails.getUsername();
            boolean isLiked = writeShortService.toggleLike(username, writeShortLikeDto.getWriteshortId());
            return ResponseEntity.ok(isLiked);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("처리 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
