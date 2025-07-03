package com.kosta.readdam.service.place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.PlaceRoomDto;
import com.kosta.readdam.dto.PlaceTimeDto;
import com.kosta.readdam.dto.place.PlaceDetailResponseDto;
import com.kosta.readdam.dto.place.PlaceEditResponseDto;
import com.kosta.readdam.dto.place.PlaceSummaryDto;
import com.kosta.readdam.dto.place.RoomDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;
import com.kosta.readdam.repository.place.PlaceDslRepository;
import com.kosta.readdam.repository.place.PlaceLikeRepository;
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
    private final PlaceLikeRepository placeLikeRepository;
    
    private static final List<String> FULL_DAY_HOURS = Arrays.asList(
    	    "00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00",
    	    "08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00",
    	    "16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"
    	);

    
    @Override
    @Transactional
    public void registerPlace(
        PlaceDto placeDto,
        List<PlaceRoomDto> roomDtoList,
        List<PlaceTimeDto> sharedTimeSlots
    ) {
        // 1. 장소 저장
        Place place = placeRepository.save(placeDto.toEntity());

        // 2. 각 방 처리
        for (PlaceRoomDto roomDto : roomDtoList) {
            roomDto.setPlaceId(place.getPlaceId());
            PlaceRoom placeRoom = placeRoomRepository.save(roomDto.toEntity(place));

            // 3. 방별 모든 시간 슬롯 생성
            for (boolean isWeekend : new boolean[]{false, true}) {
                for (String time : FULL_DAY_HOURS) {
                    boolean isSelected = sharedTimeSlots.stream().anyMatch(dto ->
                        dto.getTime().equals(time) && dto.getIsWeekend() == isWeekend
                    );

                    PlaceTime entity = PlaceTime.builder()
                            .placeRoom(placeRoom)
                            .time(time)
                            .isWeekend(isWeekend)
                            .active(isSelected)
                            .build();
                    placeTimeRepository.save(entity);
                }
            }
        }
    }

	
	@Override
    public Page<PlaceSummaryDto> getPlaceList(Pageable pageable, String keyword, String filterBy) {
        return placeDslRepository.findPlaceList(pageable, keyword, filterBy);
    }

	public PlaceEditResponseDto getPlaceEditDetail(Integer placeId) {
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
	        .basicAddress(place.getBasicAddress())
	        .detailAddress(place.getDetailAddress())
	        .phone(place.getPhone())
	        .introduce(place.getIntroduce())
	        .lat(place.getLat())
	        .lng(place.getLng())
	        .tags(tags)
	        .images(images)
	        .rooms(rooms)
	        .weekdayTimes(weekdayTimes)
	        .weekendTimes(weekendTimes)
	        .build();
	}

	@Transactional
	public void updatePlace(
	        Integer placeId,
	        PlaceDto placeDto,
	        List<PlaceRoomDto> roomDtos,
	        List<PlaceTimeDto> timeDtos
	) {
	    // 1. 기존 장소 수정
	    Place place = placeRepository.findById(placeId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));
	    place.updateFromDto(placeDto);

	    // 2. 기존 방
	    List<PlaceRoom> existingRooms = placeRoomRepository.findByPlace_PlaceId(placeId);
	    Map<Integer, PlaceRoom> existingRoomMap = existingRooms.stream()
	            .collect(Collectors.toMap(PlaceRoom::getPlaceRoomId, Function.identity()));

	    List<PlaceRoom> updatedRooms = new ArrayList<>();
	    Set<Integer> incomingRoomIds = new HashSet<>();

	    for (PlaceRoomDto roomDto : roomDtos) {
	        Integer dtoRoomId = roomDto.getPlaceRoomId();
	        if (dtoRoomId == null || !existingRoomMap.containsKey(dtoRoomId)) {
	            // 신규 방
	            PlaceRoom newRoom = roomDto.toEntity(place);
	            PlaceRoom savedRoom = placeRoomRepository.save(newRoom);
	            updatedRooms.add(savedRoom);
	            incomingRoomIds.add(savedRoom.getPlaceRoomId());
	        } else {
	            // 기존 방
	            PlaceRoom existingRoom = existingRoomMap.get(dtoRoomId);
	            existingRoom.updateFromDto(roomDto);
	            updatedRooms.add(existingRoom);
	            incomingRoomIds.add(dtoRoomId);
	        }
	    }

	    // 3. 삭제된 방 처리
	    for (PlaceRoom oldRoom : existingRooms) {
	        if (!incomingRoomIds.contains(oldRoom.getPlaceRoomId())) {
	            placeTimeRepository.deleteByPlaceRoom_PlaceRoomId(oldRoom.getPlaceRoomId());
	            placeRoomRepository.delete(oldRoom);
	        }
	    }

	    // 4. 각 방의 기존 시간 active 초기화
	    for (PlaceRoom room : updatedRooms) {
	        List<PlaceTime> existingTimes = placeTimeRepository.findByPlaceRoom_PlaceRoomId(room.getPlaceRoomId());

	        // 모든 시간 active = false
	        for (PlaceTime time : existingTimes) {
	            time.setActive(false);
	        }

	        // 선택된 시간 active = true
	        for (PlaceTime time : existingTimes) {
	            boolean isSelected = timeDtos.stream().anyMatch(dto ->
	                    dto.getTime().equals(time.getTime()) &&
	                    dto.getIsWeekend().equals(time.getIsWeekend())
	            );

	            if (isSelected) {
	                time.setActive(true);
	            }
	        }
	    }
	}




	 public List<UnifiedPlaceDto> getUnifiedList() {
	        List<Place> places = placeRepository.findAll();

	        return places.stream()
	        	    .map(p -> {
	        	        UnifiedPlaceDto dto = UnifiedPlaceDto.builder()
	        	            .id(p.getPlaceId())
	        	            .name(p.getName())
	        	            .basicAddress(p.getBasicAddress())
	        	            .img1(p.getImg1())
	        	            .tag1(p.getTag1())
	        	            .tag2(p.getTag2())
	        	            .tag3(p.getTag3())
	        	            .tag4(p.getTag4())
	        	            .tag5(p.getTag5())
	        	            .likeCount((int) placeLikeRepository.countByPlace(p))
	        	            .type("PLACE")
	        	            .build();
	        	        return dto;
	        	    })
	        	    .collect(Collectors.toList());
	    }
	
	 
	 public List<UnifiedPlaceDto> searchPlaces(
			    String tag,
			    String keyword,
			    Double lat,
			    Double lng,
			    Double radiusKm,
			    int offset,
			    int limit,
			    String sortBy
			) {
			    return placeDslRepository.searchPlaces(tag, keyword, lat, lng, radiusKm, offset, limit, sortBy);
			}

	 @Override
	 @Transactional(readOnly = true)
	 public PlaceDetailResponseDto getPlaceDetail(Integer placeId) {
	     // 1) 장소 엔티티
	     Place place = placeRepository.findById(placeId)
	         .orElseThrow(() -> new RuntimeException("해당 장소 없음"));

	     // 2) 방 정보
	     List<RoomDto> rooms = placeRoomRepository.findByPlace_PlaceId(placeId)
	         .stream().map(RoomDto::from).collect(Collectors.toList());

	     // 3) 시간대
	     List<String> weekdayTimes = placeTimeDslRepository.findTimeListByPlaceIdAndIsWeekend(placeId, false);
	     List<String> weekendTimes = placeTimeDslRepository.findTimeListByPlaceIdAndIsWeekend(placeId, true);

	     // 4) 태그
	     List<String> tags = Stream.of(
	         place.getTag1(), place.getTag2(), place.getTag3(), place.getTag4(), place.getTag5(),
	         place.getTag6(), place.getTag7(), place.getTag8(), place.getTag9(), place.getTag10()
	     ).filter(Objects::nonNull).collect(Collectors.toList());

	     // 5) 이미지
	     List<String> images = Stream.of(
	         place.getImg1(), place.getImg2(), place.getImg3(), place.getImg4(), place.getImg5(),
	         place.getImg6(), place.getImg7(), place.getImg8(), place.getImg9(), place.getImg10()
	     ).filter(Objects::nonNull).collect(Collectors.toList());

	     // 6) 좋아요 개수
	     int likeCount = placeLikeRepository.countByPlace_PlaceId(placeId);

	     // 7) 유저 좋아요 여부
	     boolean liked = false;
	     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	     if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
	         String username = auth.getName();
	         liked = placeLikeRepository.existsByPlace_PlaceIdAndUser_Username(placeId, username);
	     }

	     // 8) 반환
	     return PlaceDetailResponseDto.builder()
	         .name(place.getName())
	         .basicAddress(place.getBasicAddress())
	         .detailAddress(place.getDetailAddress())
	         .phone(place.getPhone())
	         .introduce(place.getIntroduce())
	         .lat(place.getLat())
	         .lng(place.getLng())
	         .tags(tags)
	         .images(images)
	         .rooms(rooms)
	         .weekdayTimes(weekdayTimes)
	         .weekendTimes(weekendTimes)
	         .likeCount(likeCount)
	         .liked(liked)
	         .build();
	 }
}
