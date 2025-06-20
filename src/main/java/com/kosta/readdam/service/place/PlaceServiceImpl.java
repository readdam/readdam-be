package com.kosta.readdam.service.place;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.PlaceRoomDto;
import com.kosta.readdam.dto.PlaceTimeDto;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.repository.place.PlaceRepository;
import com.kosta.readdam.repository.place.PlaceRoomRepository;
import com.kosta.readdam.repository.place.PlaceTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
	
    private final PlaceRepository placeRepository;
    private final PlaceRoomRepository placeRoomRepository;
    private final PlaceTimeRepository placeTimeRepository;
    
	@Override
	@Transactional
	public void registerPlace(PlaceDto placeDto, List<PlaceRoomDto> roomDtoList, List<PlaceTimeDto> sharedTimeSlots) {
		// 1. 장소 저장
	    Place place = placeRepository.save(placeDto.toEntity());

	    // 2. 각 방 처리
	    for (PlaceRoomDto roomDto : roomDtoList) {
	        roomDto.setPlaceId(place.getPlaceId());
	        PlaceRoom placeRoom = placeRoomRepository.save(roomDto.toEntity(place));

	        // 3. 시간대 공통 적용
	        for (PlaceTimeDto timeDto : sharedTimeSlots) {
	            timeDto.setPlaceRoomId(placeRoom.getPlaceRoomId()); // 실제 DB에서 받은 ID로 설정
	            placeTimeRepository.save(timeDto.toEntity(placeRoom));
	        }
	    }

	}

}
