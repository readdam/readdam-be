package com.kosta.readdam.controller.home;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.service.write.WriteService;
import com.kosta.readdam.service.write.WriteShortService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

	private final WriteService writeService;
	private final WriteShortService writeShortService;

	// 글쓰기 최신순 4개 가져오기
	@GetMapping("/writes")
	public ResponseEntity<?> getLatestWrites(@RequestParam(defaultValue = "4") int limit) {
		try {
			List<WriteDto> writes = writeService.findLatest(limit);
			return ResponseEntity.ok(writes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("글쓰기 최신 데이터를 불러오던 중 오류가 발생했습니다.");
		}
	}

	// 읽담한줄 최신순 5개 가져오기
	@GetMapping("/shorts")
	public ResponseEntity<?> getLatestWriteShorts(
        @RequestParam(defaultValue = "5") int limit,
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
    try {
        String username = (principalDetails != null) ? principalDetails.getUsername() : null;
        List<WriteShortDto> writeShorts = writeShortService.findLatest(limit, username);
        return ResponseEntity.ok(writeShorts);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("읽담한줄 최신 데이터를 불러오던 중 오류가 발생했습니다.");
    }
}

}
