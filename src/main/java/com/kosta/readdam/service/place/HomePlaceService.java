package com.kosta.readdam.service.place;

import java.util.List;

import com.kosta.readdam.dto.place.UnifiedPlaceDto;

public interface HomePlaceService {
    List<UnifiedPlaceDto> getLatestPlaces(int limit) throws Exception;
    List<UnifiedPlaceDto> getPlacesByDistance(Double lat, Double lng, int limit) throws Exception;

}
