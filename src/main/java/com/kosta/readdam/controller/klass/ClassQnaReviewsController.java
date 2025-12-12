package com.kosta.readdam.controller.klass;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.ClassQnaDto;
import com.kosta.readdam.dto.ClassReviewDto;
import com.kosta.readdam.service.klass.ClassQnAReviewsService;

@RestController
public class ClassQnaReviewsController {
	
	@Autowired
	private ClassQnAReviewsService classQRService;
	
	@PostMapping("/classQna")
	public ResponseEntity<?> createQna(@RequestBody ClassQnaDto classQnaDto, 
			@AuthenticationPrincipal PrincipalDetails principal){
		try {
			String username = principal.getUsername();
			classQRService.createQna(classQnaDto, username);
			return ResponseEntity.ok().body("질문 등록 완료");
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 등록 실패: "+e.getMessage());
		}
	}
	
	@GetMapping("/classQna/{classId}")
	public ResponseEntity<?> getQnaList(@PathVariable Integer classId) {
		try {
			List<ClassQnaDto> list = classQRService.getQnaList(classId);
			return ResponseEntity.ok().body(Map.of("data",list));
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Q&A 목록 조회 실패: "+e.getMessage());
		}
	}
	
	@PostMapping("/classQnaAnswer")
	public ResponseEntity<?> answerQna(@RequestBody Map<String, Object> payload, 
			@AuthenticationPrincipal PrincipalDetails principal) {
		try {
			Integer classQnaId = (Integer) payload.get("classQnaId");
			String username = principal.getUsername();
			String answer = (String) payload.get("answer");
			
			classQRService.answerQna(classQnaId, answer, username);
			return ResponseEntity.ok().body("답변 등록 완료");
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("답변 등록 실패: "+e.getMessage());
		}
	}
	
	@GetMapping("/classReview/{classId}")
	public ResponseEntity<?> getReviewList(@PathVariable Integer classId) {
		try {
			List<ClassReviewDto> reviews = classQRService.getReviewsByClassId(classId);
			return ResponseEntity.ok().body(Map.of("status", "success", "data", reviews));
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("리뷰 목록 조회 실패: "+e.getMessage());
		}
	}
	
	@PostMapping("/classReview")
	public ResponseEntity<?> createReview(@AuthenticationPrincipal PrincipalDetails principal,
			@RequestParam("classId") Integer classId,
			@RequestParam("content") String content,
			@RequestParam("rating") Integer rating,
			@RequestParam("img") String img,
			@RequestParam(name = "ifile", required = false) MultipartFile ifile){
		try {
			classQRService.createReview(principal.getUser(), classId, content, rating, img, ifile);
			return ResponseEntity.ok().body("리뷰 등록 완료");
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("리뷰 등록 실패: " +e.getMessage());
		}
	}

}
