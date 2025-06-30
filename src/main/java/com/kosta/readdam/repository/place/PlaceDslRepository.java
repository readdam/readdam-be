package com.kosta.readdam.repository.place;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.place.PlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;

public interface PlaceDslRepository {
	Page<PlaceSummaryDto> findPlaceList(Pageable pageable, String keyword, String filterBy);
	List<UnifiedPlaceDto> searchPlaces(
	        String tag,
	        String keyword,
	        Double lat,
	        Double lng,
	        Double radiusKm,
	        int offset,
	        int limit
	    );
}
