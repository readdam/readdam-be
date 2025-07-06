package com.kosta.readdam.controller.otherPlace;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.service.otherPlace.OtherPlaceLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/otherplace/likes")
@RequiredArgsConstructor
public class OtherPlaceLikeController {
	private final OtherPlaceLikeService otherPlaceLikeService;

    /**
     * 외부 장소 좋아요 토글
     */
    @PostMapping("/{otherPlaceId}")
    public ResponseEntity<String> toggleLike(
            @PathVariable Integer otherPlaceId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        boolean liked = otherPlaceLikeService.toggleLike(username, otherPlaceId);
        return ResponseEntity.ok(liked ? "liked" : "unliked");
    }

    /**
     * 외부 장소 좋아요 여부 조회
     */
    @GetMapping("/{otherPlaceId}")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Integer otherPlaceId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        boolean liked = otherPlaceLikeService.isLiked(username, otherPlaceId);
        return ResponseEntity.ok(liked);
    }

    /**
     * 외부 장소 좋아요 수 조회
     */
    @GetMapping("/{otherPlaceId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Integer otherPlaceId) {
        long count = otherPlaceLikeService.countLikes(otherPlaceId);
        return ResponseEntity.ok(count);
    }
}
