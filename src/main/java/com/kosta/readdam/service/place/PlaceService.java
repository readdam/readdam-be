package com.kosta.readdam.service.place;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.PlaceRoomDto;
import com.kosta.readdam.dto.PlaceTimeDto;
import com.kosta.readdam.dto.place.PlaceEditResponseDto;
import com.kosta.readdam.dto.place.PlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;

public interface PlaceService {
	void registerPlace(PlaceDto placeDto, List<PlaceRoomDto> roomDtoList, List<PlaceTimeDto> sharedTimeSlots);
	Page<PlaceSummaryDto> getPlaceList(Pageable pageable, String keyword, String filterBy);
	PlaceEditResponseDto getPlaceDetail(Integer placeId);
	void updatePlace(Integer placeId, PlaceDto placeDto, List<PlaceRoomDto> roomDtos, List<PlaceTimeDto> timeDtos);
	List<UnifiedPlaceDto> getUnifiedList();
//	List<UnifiedPlaceDto> searchPlaces(String tag, String keyword, Double userLat, Double userLng, Double radiusKm);
	List<UnifiedPlaceDto> searchPlaces(String tag, String keyword, Double lat, Double lng, Double radiusKm, int offset, int limit);
}

