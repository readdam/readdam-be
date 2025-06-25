package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.PlaceDto;

public interface MyPlaceLikeService {
	
	List<PlaceDto> getLikedPlaces(String username) throws Exception;
    PlaceDto toggleLike(String username, Integer placeId) throws Exception;

}
