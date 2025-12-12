package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BookReviewDto;
import com.kosta.readdam.dto.ClassReviewDto;
import com.kosta.readdam.service.my.MyBookReviewService;
import com.kosta.readdam.service.my.MyClassReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyReviewController {

    private final MyBookReviewService myBookReviewService;
    private final MyClassReviewService myClassReviewService;

    @GetMapping("/reviewBook")
    public ResponseEntity<?> getMyBookReviews(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        try {
            String username = principalDetails.getUsername();
            List<BookReviewDto> reviews = myBookReviewService.getReviewsByUsername(username);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("내 책 후기 조회 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/reviewClass")
    public ResponseEntity<?> getMyClassReviews(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        try {
            String username = principalDetails.getUsername();
            List<ClassReviewDto> reviews = myClassReviewService.getMyReviews(username);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("내 모임 후기 조회 중 오류가 발생했습니다.");
        }
    }
}
