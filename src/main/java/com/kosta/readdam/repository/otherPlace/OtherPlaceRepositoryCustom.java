package com.kosta.readdam.repository.otherPlace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;

public interface OtherPlaceRepositoryCustom {
	Page<OtherPlaceSummaryDto> findAllByFilter(Pageable pageable, String keyword, String filterBy);
}
