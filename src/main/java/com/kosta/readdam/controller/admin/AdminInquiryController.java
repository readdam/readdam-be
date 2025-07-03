package com.kosta.readdam.controller.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.service.admin.AdminInquiryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/inquiry")
@RequiredArgsConstructor
public class AdminInquiryController {
	
	private final AdminInquiryService adminInquiryService;

	 @GetMapping
	    public ResponseEntity<PagedResponse<InquiryDto>> list(
	            @RequestParam(value = "filterType", required = false, defaultValue = "title") String filterType,
	            @RequestParam(value = "keyword",    required = false) String keyword,
	            @RequestParam(value = "startDate",  required = false)
	            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	            @RequestParam(value = "endDate",    required = false)
	            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	            @RequestParam(value = "status",     required = false) String status,
	            @RequestParam(value = "page",       defaultValue = "0") int page,
	            @RequestParam(value = "size",       defaultValue = "10") int size
	    ) {
	        PagedResponse<InquiryDto> resp = adminInquiryService.getInquiries(
	                filterType, keyword, startDate, endDate, status, page, size);
	        return ResponseEntity.ok(resp);
	    }

	    @PatchMapping("/{id}/answer")
	    public ResponseEntity<Void> answer(
	            @PathVariable("id")   Integer inquiryId,
	            @RequestBody AnswerRequest req
	    ) {
	        adminInquiryService.answerInquiry(inquiryId, req.getAnswer());
	        return ResponseEntity.noContent().build();
	    }

	    // 요청 바디용 DTO
	    public static class AnswerRequest {
	        private String answer;
	        public String getAnswer() { return answer; }
	        public void setAnswer(String answer) { this.answer = answer; }
	    }

}
