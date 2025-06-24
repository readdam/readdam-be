package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.service.my.MyWriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyWriteController {

	private final MyWriteService myWriteService;

	@GetMapping("/myWrite")
	public ResponseEntity<List<WriteDto>> getMyWrites(@AuthenticationPrincipal PrincipalDetails principalDetails)
			throws Exception {
		String username = principalDetails.getUsername();
		return ResponseEntity.ok(myWriteService.getMyWrites(username));
	}
	
	 @GetMapping("/myWriteComment")
	    public ResponseEntity<List<WriteCommentDto>> getMyWriteComments(
	            @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {

	        String username = principalDetails.getUsername();
	        List<WriteCommentDto> comments = myWriteService.getMyWriteComments(username);
	        return ResponseEntity.ok(comments);
	    }

}
