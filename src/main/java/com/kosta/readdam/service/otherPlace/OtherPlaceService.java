package com.kosta.readdam.service.otherPlace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;

public interface OtherPlaceService {
	void save(OtherPlaceDto dto);
	OtherPlaceDto getOtherPlaceDetail(Integer id);
	Page<OtherPlaceSummaryDto> getOtherPlaceList(Pageable pageable, String keyword, String filterBy);
	void updateOtherPlace(Integer id, OtherPlaceDto dto);
	List<UnifiedPlaceDto> getUnifiedList();
//	List<UnifiedPlaceDto> searchPlaces(String tag, String keyword, Double userLat, Double userLng, Double radiusKm);
	List<UnifiedPlaceDto> searchPlaces(String tag, String keyword, Double lat, Double lng, Double radiusKm, int offset,
			int limit);
}
