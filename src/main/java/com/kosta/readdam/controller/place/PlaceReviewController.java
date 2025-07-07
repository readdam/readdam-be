package com.kosta.readdam.controller.place;

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
import com.kosta.readdam.dto.PlaceReviewDto;
import com.kosta.readdam.dto.place.PlaceReviewPageResponse;
import com.kosta.readdam.dto.place.PlaceReviewRequest;
import com.kosta.readdam.dto.place.PlaceReviewUpdateRequest;
import com.kosta.readdam.service.place.PlaceReviewService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/place/reviews")
@RequiredArgsConstructor
public class PlaceReviewController {
	private final PlaceReviewService placeReviewService;

	@PostMapping
    public ResponseEntity<PlaceReviewDto> writeReview(
            @RequestBody PlaceReviewRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        PlaceReviewDto dto = placeReviewService.writeReview(username, request);
        return ResponseEntity.ok(dto);
    }
	
	@GetMapping
	public ResponseEntity<PlaceReviewPageResponse> getReviews(
	        @RequestParam Integer placeId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        @AuthenticationPrincipal PrincipalDetails principal
	) {
	    String username = principal != null ? principal.getUsername() : null;
	    Page<PlaceReviewDto> reviewsPage = placeReviewService.getReviews(placeId, username, page, size);

	    PlaceReviewPageResponse response = PlaceReviewPageResponse.builder()
	            .content(reviewsPage.getContent())
	            .pageInfo(PageInfo2.from(reviewsPage))
	            .build();

	    return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @RequestBody PlaceReviewUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        String username = principal.getUsername();
        placeReviewService.updateReview(id, username, request.getContent(), request.getRating(), request.getIsHide());
        return ResponseEntity.ok().body("리뷰가 수정되었습니다.");
    }

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteReview(
	        @PathVariable Integer id,
	        @AuthenticationPrincipal PrincipalDetails principal
	) {
	    String username = principal.getUsername();
	    placeReviewService.deleteReview(id, username);
	    return ResponseEntity.ok().body("리뷰가 삭제되었습니다.");
	}

}
