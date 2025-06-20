package com.kosta.readdam.service.place;

import java.util.List;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.PlaceRoomDto;
import com.kosta.readdam.dto.PlaceTimeDto;

public interface PlaceService {
	void registerPlace(PlaceDto placeDto, List<PlaceRoomDto> roomDtoList, List<PlaceTimeDto> sharedTimeSlots);
}

