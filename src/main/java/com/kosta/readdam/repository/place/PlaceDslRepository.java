package com.kosta.readdam.repository.place;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.place.PlaceSummaryDto;

public interface PlaceDslRepository {
	Page<PlaceSummaryDto> findPlaceList(Pageable pageable, String keyword, String filterBy);
}
