package com.kosta.readdam.controller.place;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.service.place.PlaceLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/place/likes")
@RequiredArgsConstructor
public class PlaceLikeController {
	private final PlaceLikeService placeLikeService;

    // 좋아요 토글
    @PostMapping("/{placeId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable Integer placeId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        boolean liked = placeLikeService.toggleLike(username, placeId);
        return ResponseEntity.ok().body(liked ? "liked" : "unliked");
    }

    // 좋아요 여부 조회
    @GetMapping("/{placeId}")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Integer placeId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        boolean liked = placeLikeService.isLiked(username, placeId);
        return ResponseEntity.ok(liked);
    }

    // 좋아요 수 조회
    @GetMapping("/{placeId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Integer placeId) {
        long count = placeLikeService.countLikes(placeId);
        return ResponseEntity.ok(count);
    }
}
