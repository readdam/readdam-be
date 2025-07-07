package com.kosta.readdam.controller.otherPlace;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.OtherPlaceReviewDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceReviewPageResponse;
import com.kosta.readdam.dto.place.PlaceReviewRequest;
import com.kosta.readdam.dto.place.PlaceReviewUpdateRequest;
import com.kosta.readdam.service.otherPlace.OtherPlaceReviewService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/otherplace/reviews")
@RequiredArgsConstructor
public class OtherPlaceReviewController {
	private final OtherPlaceReviewService otherPlaceReviewService;

    /**
     * 외부 장소 리뷰 작성
     */
    @PostMapping
    public ResponseEntity<OtherPlaceReviewDto> writeReview(
            @RequestBody PlaceReviewRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        OtherPlaceReviewDto dto = otherPlaceReviewService.writeReview(username, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * 외부 장소 리뷰 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<OtherPlaceReviewPageResponse> getReviews(
            @RequestParam Integer otherPlaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal != null ? principal.getUsername() : null;
        Page<OtherPlaceReviewDto> reviewsPage = otherPlaceReviewService.getReviews(otherPlaceId, username, page, size);

        OtherPlaceReviewPageResponse response = OtherPlaceReviewPageResponse.builder()
                .content(reviewsPage.getContent())
                .pageInfo(PageInfo2.from(reviewsPage))
                .build();

        return ResponseEntity.ok(response);
    }	

    /**
     * 외부 장소 리뷰 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @RequestBody PlaceReviewUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        otherPlaceReviewService.updateReview(id, username, request.getContent(), request.getRating(), request.getIsHide());
        return ResponseEntity.ok("리뷰가 수정되었습니다.");
    }

    /**
     * 외부 장소 리뷰 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Integer id,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        otherPlaceReviewService.deleteReview(id, username);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}
