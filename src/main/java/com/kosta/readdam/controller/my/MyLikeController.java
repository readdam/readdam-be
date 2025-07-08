package com.kosta.readdam.controller.my;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.service.my.MyBookLikeService;
import com.kosta.readdam.service.my.MyClassLikeService;
import com.kosta.readdam.service.my.MyPlaceLikeService;
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
    private final MyPlaceLikeService myPlaceLikeService;
    private final MyClassLikeService myClassLikeService;

    
    @GetMapping("/likeBook")
    public Page<BookDto> getLikedBooks(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {
        return myBookLikeService.getLikedBooksByUsername(user.getUsername(), page, size);
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
            //@RequestParam Integer writeId
            @RequestBody Map<String, String> payload // Map<String, String> 으로 받기
    ) {
        String username = principalDetails.getUsername();
        try {
            //String result = myWriteLikeService.toggleLike(username, writeId);
            //return ResponseEntity.ok(result);
        	Integer writeId = Integer.parseInt(payload.get("writeId")); // 👈 직접 변환
        	boolean liked = myWriteLikeService.toggleLike(username, writeId);
        	return ResponseEntity.ok(liked); // ✅ true 또는 false
        } catch (Exception e) {
            log.error("좋아요 토글 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 토글 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/likePlace")
    public ResponseEntity<?> getLikedPlaces(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        try {
            List<UnifiedPlaceDto> likedPlaces = myPlaceLikeService.getLikedPlaces(username);
            return ResponseEntity.ok(likedPlaces);
        } catch (Exception e) {
            log.error("좋아요 장소 조회 실패", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 장소 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /** 좋아요 토글 (장소) */
    @PostMapping("/unified-place-like")
    public ResponseEntity<?> toggleUnifiedPlaceLike(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam Integer id,
            @RequestParam String type  // "PLACE" or "OTHER"
    ) {
        String username = principalDetails.getUsername();
        try {
            UnifiedPlaceDto dto = myPlaceLikeService.toggleUnifiedLike(username, id, type);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("좋아요 토글 실패 (통합)", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("좋아요 토글 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/likeClass")
    public ResponseEntity<?> getLikedClasses(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String username = principalDetails.getUsername();
        try {
            return ResponseEntity.ok(myClassLikeService.getLikedClasses(username));
        } catch (Exception e) {
            log.error("좋아요 모임 조회 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 모임 조회 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/class-like")
    public ResponseEntity<?> toggleClassLike(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam Integer classId
    ) {
        String username = principalDetails.getUsername();
        try {
            return ResponseEntity.ok(myClassLikeService.toggleLike(username, classId));
        } catch (Exception e) {
            log.error("모임 좋아요 토글 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토글 중 오류 발생: " + e.getMessage());
        }
    }
}
