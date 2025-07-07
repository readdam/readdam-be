package com.kosta.readdam.repository.otherPlace;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.OtherPlaceReviewDto;

public interface OtherPlaceReviewDslRepository {
	Page<OtherPlaceReviewDto> findVisibleReviews(Integer otherPlaceId, String username, int page, int size);
}
