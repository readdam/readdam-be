package com.kosta.readdam.service.place;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.PlaceReviewDto;
import com.kosta.readdam.dto.place.PlaceReviewRequest;

public interface PlaceReviewService {
	PlaceReviewDto writeReview(String username, PlaceReviewRequest request);
	Page<PlaceReviewDto> getReviews(Integer placeId, String username, int page, int size);
}
