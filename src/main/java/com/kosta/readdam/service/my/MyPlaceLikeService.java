package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;

public interface MyPlaceLikeService {

	List<UnifiedPlaceDto> getLikedPlaces(String username);

	UnifiedPlaceDto toggleUnifiedLike(String username, Integer id, String type) throws Exception;
	


}
