package com.kosta.readdam.service.place;

import java.util.List;

import com.kosta.readdam.dto.place.HomePlaceSummaryDto;

public interface HomePlaceService {
    List<HomePlaceSummaryDto> getLatestPlaces(int limit) throws Exception;
    List<HomePlaceSummaryDto> getPlacesByDistance(Double lat, Double lng, int limit) throws Exception;

}
