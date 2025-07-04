package com.kosta.readdam.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.entity.Notice;
import com.kosta.readdam.service.admin.AdminNoticeService;

@RestController
@RequestMapping("/admin")
public class AdminNoticeController {
	
	@Autowired
	private AdminNoticeService noticeService;
	
	public AdminNoticeController(AdminNoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	
	@PostMapping("/createNotice")
	public ResponseEntity<?> createNotice(@ModelAttribute NoticeDto dto) {
		try {
			Notice saved = noticeService.createNotice(dto);
			return ResponseEntity.ok(saved);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("공지사항 등록 중 오류가 발생했습니다: "+e.getMessage());
		}
		
	}
	
	@GetMapping("/notices")
	public ResponseEntity<?> getAllNotices() {
		try {
			List<NoticeDto> list = noticeService.getAllNotices();
			return ResponseEntity.ok(list); 
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("공지사항 목록 조회 실패: "+e.getMessage());
		}
	}
	
	@GetMapping("/notice/{noticeId}")
	public ResponseEntity<?> getNoticeById(@PathVariable Integer noticeId) {
		try {
			Notice notice = noticeService.getNoticeById(noticeId);
			NoticeDto dto = NoticeDto.fromEntity(notice);
			return ResponseEntity.ok(dto);
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("공지사항을 찾을 수 없습니다: " + e.getMessage());
		}
	}
	

}
