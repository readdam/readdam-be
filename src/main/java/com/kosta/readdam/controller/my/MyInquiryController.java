package com.kosta.readdam.controller.my;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.service.my.MyInquiryService;

@RestController
@RequestMapping("/my")
public class MyInquiryController {
	
	@Autowired
	private MyInquiryService myInquiryService;

	@GetMapping("/myInquiryList")
    public PagedResponse<InquiryDto> getMyInquiryList(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return myInquiryService.getMyInquiryList(user.getUsername(), page, size);
    }
	
	@PostMapping("/myInquiryWrite")
	public ResponseEntity<?> writeInquiry(
	        @AuthenticationPrincipal PrincipalDetails principalDetails,
	        @RequestBody InquiryDto dto) {
	    try {
	        String username = principalDetails.getUsername();
	        InquiryDto savedDto = myInquiryService.writeInquiry(username, dto);
	        return ResponseEntity.ok(savedDto); // ✅ 전체 DTO 반환
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성 실패");
	    }
	}
	
	@PostMapping("/myInquiryUpdate")
	public ResponseEntity<?> updateInquiry(@AuthenticationPrincipal PrincipalDetails principalDetails,
	                                       @RequestBody InquiryDto dto) {
	    try {
	        String username = principalDetails.getUsername();
	        InquiryDto updated = myInquiryService.updateInquiry(username, dto);
	        return ResponseEntity.ok(updated);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정 실패: " + e.getMessage());
	    }
	}

	@PostMapping("/myInquiryDelete/{id}")
	public ResponseEntity<?> deleteInquiry(@AuthenticationPrincipal PrincipalDetails principalDetails,
	                                       @PathVariable("id") Integer inquiryId) {
	    try {
	        String username = principalDetails.getUsername();
	        myInquiryService.deleteInquiry(username, inquiryId);
	        return ResponseEntity.ok("삭제 완료");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제 실패: " + e.getMessage());
	    }
	}




}
