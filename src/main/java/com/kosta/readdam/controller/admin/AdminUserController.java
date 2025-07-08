package com.kosta.readdam.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.UserSearchResponse;
import com.kosta.readdam.service.admin.AdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {
	
	private final AdminUserService adminUserService;
	
	@GetMapping("/userList")
	public ResponseEntity<?> getUsers(
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue = "10") Integer size
			){
		try {
			if(keyword == null || keyword.trim().isEmpty()) {
				return ResponseEntity
						.badRequest()
						.body("검색어를 입력하세요.");
			}
			UserSearchResponse result = adminUserService.getUserList(keyword, page, size);
			return ResponseEntity.ok(result);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity
					.badRequest()
					.body("요청 파라미터가 잘못됐습니다: "+e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("서버 오류가 발생했습니다.");
		}
	}

}
