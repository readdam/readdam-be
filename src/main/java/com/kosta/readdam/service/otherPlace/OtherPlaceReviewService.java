package com.kosta.readdam.service.otherPlace;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.OtherPlaceReviewDto;
import com.kosta.readdam.dto.place.PlaceReviewRequest;

public interface OtherPlaceReviewService {
	OtherPlaceReviewDto writeReview(String username, PlaceReviewRequest request);
    Page<OtherPlaceReviewDto> getReviews(Integer otherPlaceId, String username, int page, int size);
    void updateReview(Integer id, String username, String content, Integer rating, Boolean isHide);
    void deleteReview(Integer id, String username);
}
