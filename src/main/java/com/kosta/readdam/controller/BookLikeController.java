package com.kosta.readdam.controller;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.service.BookLikeService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookLikeController {

    private final BookLikeService bookLikeService;
    
    @PostMapping("/book-like")
    public ResponseEntity<?> toggleBookLike(@RequestParam String bookIsbn,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return new ResponseEntity<>("로그인 필요", HttpStatus.UNAUTHORIZED);
        }

        try {
            String username = principalDetails.getUsername();
            boolean liked = bookLikeService.toggleLike(username, bookIsbn);
            return ResponseEntity.ok(liked ? "좋아요 완료" : "좋아요 취소");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("처리 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
