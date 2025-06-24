package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.service.my.MyBookLikeService;
import com.kosta.readdam.service.my.MyWriteLikeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyLikeController {

    private final MyBookLikeService myBookLikeService;
    private final MyWriteLikeService myWriteLikeService;

    /** 좋아요한 책 목록 조회 */
    @GetMapping("/likeBook")
    public ResponseEntity<?> getLikedBooks(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        try {
            List<BookDto> likedBooks = myBookLikeService.getLikedBooksByUsername(username);
            return ResponseEntity.ok(likedBooks);
        } catch (Exception e) {
            log.error("좋아요 책 조회 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 책 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/likeWrite")
    public ResponseEntity<?> getLikedWrites(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        try {
            List<WriteDto> liked = myWriteLikeService.getLikedWrites(username);
            return ResponseEntity.ok(liked);
        } catch (Exception e) {
            log.error("좋아요 글 조회 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /** 좋아요 토글 */
    @PostMapping("/write-like")
    public ResponseEntity<?> toggleLike(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam Integer writeId
    ) {
        String username = principalDetails.getUsername();
        try {
            String result = myWriteLikeService.toggleLike(username, writeId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("좋아요 토글 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 토글 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
