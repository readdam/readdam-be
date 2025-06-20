package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BookLikeDto;
import com.kosta.readdam.service.my.MyBookLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyLikeController {

    private final MyBookLikeService myBookLikeService;

    @GetMapping("/likeBook")
    public ResponseEntity<?> getLikedBooks(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        try {
            // JWT로부터 꺼낸 로그인한 사용자 아이디
            String username = principalDetails.getUsername();

            List<BookLikeDto> liked = myBookLikeService.getLikedBooksByUsername(username);
            return ResponseEntity.ok(liked);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 도서 목록 조회 실패: " + e.getMessage());
        }
    }
}
