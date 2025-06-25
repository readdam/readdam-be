package com.kosta.readdam.service.place;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.PlaceRoomDto;
import com.kosta.readdam.dto.PlaceTimeDto;
import com.kosta.readdam.dto.place.PlaceEditResponseDto;
import com.kosta.readdam.dto.place.PlaceSummaryDto;
import com.kosta.readdam.dto.place.RoomDto;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;
import com.kosta.readdam.repository.place.PlaceDslRepository;
import com.kosta.readdam.repository.place.PlaceRepository;
import com.kosta.readdam.repository.place.PlaceRoomRepository;
import com.kosta.readdam.repository.place.PlaceTimeDslRepository;
import com.kosta.readdam.repository.place.PlaceTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
	
    private final PlaceRepository placeRepository;
    private final PlaceRoomRepository placeRoomRepository;
    private final PlaceTimeRepository placeTimeRepository;
    private final PlaceDslRepository placeDslRepository;
    private final PlaceTimeDslRepository placeTimeDslRepository;

    
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
	
	@Override
    public Page<PlaceSummaryDto> getPlaceList(Pageable pageable, String keyword, String filterBy) {
        return placeDslRepository.findPlaceList(pageable, keyword, filterBy);
    }

	public PlaceEditResponseDto getPlaceDetail(Integer placeId) {
	    Place place = placeRepository.findById(placeId)
	        .orElseThrow(() -> new RuntimeException("해당 장소 없음"));

	    // ✅ 방 정보
	    List<RoomDto> rooms = placeRoomRepository.findByPlace_PlaceId(placeId)
	        .stream().map(RoomDto::from).collect(Collectors.toList());

	    // ✅ 시간대
	    List<String> weekdayTimes = placeTimeDslRepository.findTimeListByPlaceIdAndIsWeekend(placeId, false);
	    List<String> weekendTimes = placeTimeDslRepository.findTimeListByPlaceIdAndIsWeekend(placeId, true);

	    // ✅ 태그
	    List<String> tags = Stream.of(
	        place.getTag1(), place.getTag2(), place.getTag3(), place.getTag4(), place.getTag5(),
	        place.getTag6(), place.getTag7(), place.getTag8(), place.getTag9(), place.getTag10()
	    ).filter(Objects::nonNull).collect(Collectors.toList());

	    // ✅ 이미지
	    List<String> images = Stream.of(
	        place.getImg1(), place.getImg2(), place.getImg3(), place.getImg4(), place.getImg5(),
	        place.getImg6(), place.getImg7(), place.getImg8(), place.getImg9(), place.getImg10()
	    ).filter(Objects::nonNull).collect(Collectors.toList());

	    return PlaceEditResponseDto.builder()
	        .name(place.getName())
	        .location(place.getLocation())
	        .phone(place.getPhone())
	        .introduce(place.getIntroduce())
	        .lat(place.getLat())
	        .log(place.getLog())
	        .tags(tags)
	        .images(images)
	        .rooms(rooms)
	        .weekdayTimes(weekdayTimes)
	        .weekendTimes(weekendTimes)
	        .build();
	}


}
