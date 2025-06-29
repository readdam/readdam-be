package com.kosta.readdam.controller.klass;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.service.klass.ClassLikeService;

@RestController
public class ClassLikeController {
	
	@Autowired
	private ClassLikeService classLikeService;
	
	// 좋아요 상태 조회
	@GetMapping("/classDetail/{classId}/like-status")
	public ResponseEntity<?> getLikeStatus(@PathVariable Integer classId, 
			@AuthenticationPrincipal PrincipalDetails principal){
		 try {
			 String username = principal.getUsername();
			 Map<String, Object> result = classLikeService.getLikeStatus(classId, username);
			 return ResponseEntity.ok(result);
		 }catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("좋아요 상태 조회 실패: " + e.getMessage());
		}
	}
	
	//좋아요 토글
	@PostMapping("/classDetail/{classId}/like")
	public ResponseEntity<?> toggleLike(@PathVariable Integer classId,
			@AuthenticationPrincipal PrincipalDetails principal) {
		try {
			String username = principal.getUsername();
			Map<String, Object> result = classLikeService.toggleLike(classId, username);
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좋아요 처리 실패: " + e.getMessage());
		}
	}

}
