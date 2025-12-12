package com.kosta.readdam.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.ClassListDto;
import com.kosta.readdam.service.klass.ClassListService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/classList")
@RequiredArgsConstructor
public class AdminClassController {
	private final ClassListService classListService;

	@GetMapping
	public ResponseEntity<?> getClassList(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String status, @RequestParam(required = false) String period,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
		Page<ClassListDto> result = classListService.getClassList(keyword, status, period, pageable);

		// pageInfo 형식으로 응답 포장 (PageInfo2 사용)
		return ResponseEntity.ok(new java.util.HashMap<String, Object>() {
			{
				put("content", result.getContent());
				put("pageInfo", com.kosta.readdam.util.PageInfo2.from(result));
			}
		});
	}
}
