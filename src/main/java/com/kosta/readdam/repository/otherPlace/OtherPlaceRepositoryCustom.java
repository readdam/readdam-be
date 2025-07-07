package com.kosta.readdam.repository.otherPlace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;

public interface OtherPlaceRepositoryCustom {
	Page<OtherPlaceSummaryDto> findAllByFilter(Pageable pageable, String keyword, String filterBy);
	List<UnifiedPlaceDto> searchPlaces(String tag, String keyword, Double lat, Double lng, Double radiusKm, int offset,
			int limit, String sortBy);
	List<PlaceDto> searchForAll(String keyword, String sort, int limit);
}
