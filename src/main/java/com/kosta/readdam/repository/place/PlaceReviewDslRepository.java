package com.kosta.readdam.repository.place;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.PlaceReviewDto;

public interface PlaceReviewDslRepository {
	Page<PlaceReviewDto> findVisibleReviews(Integer placeId, String username, int page, int size);
}