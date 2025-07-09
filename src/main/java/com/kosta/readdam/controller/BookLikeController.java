package com.kosta.readdam.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.service.BookLikeService;

import lombok.RequiredArgsConstructor;

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
    
    @GetMapping("/my/liked")
    public ResponseEntity<List<String>> getLikedBooks(
            @RequestParam String query,
            @RequestParam(required = false) String target,
            @RequestParam(defaultValue = "accuracy") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal PrincipalDetails principalDetails // Spring Security에서 로그인 유저 주입
    ) {
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
    	
    	if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principalDetails.getUsername();


        List<String> likedIsbns = bookLikeService.getLikedIsbns(
        		username, query, target, sort, page, size);
        
        System.out.println(likedIsbns);
        return ResponseEntity.ok(likedIsbns);
    }

    @GetMapping("/book-like/check")
    public ResponseEntity<?> checkBookLike(@RequestParam String bookIsbn,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return new ResponseEntity<>("로그인 필요", HttpStatus.UNAUTHORIZED);
        }

        try {
            String username = principalDetails.getUsername();
            boolean liked = bookLikeService.isBookLiked(username, bookIsbn);
            return ResponseEntity.ok(liked);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("처리 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
