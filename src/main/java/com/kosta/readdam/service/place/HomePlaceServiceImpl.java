package com.kosta.readdam.service.place;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.repository.otherPlace.OtherPlaceLikeRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;
import com.kosta.readdam.repository.place.PlaceLikeRepository;
import com.kosta.readdam.repository.place.PlaceRepository;
import com.kosta.readdam.util.DistanceUtil;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class HomePlaceServiceImpl implements HomePlaceService {
	
    private final PlaceRepository placeRepository;
    private final OtherPlaceRepository otherPlaceRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final OtherPlaceLikeRepository otherPlaceLikeRepository;

    // 홈화면 최신순 장소 리스트 조회 (Place + OtherPlace)
	@Override
	public List<UnifiedPlaceDto> getLatestPlaces(int limit) throws Exception {
        
        // Place 테이블에서 최신순 (PK 내림차순)으로 limit 만큼 조회
        List<Place> placeList = placeRepository.findAllByOrderByPlaceIdDesc(PageRequest.of(0, limit));

        // OtherPlace 테이블에서 최신순 (PK 내림차순)으로 limit 만큼 조회
        List<OtherPlace> otherPlaceList = otherPlaceRepository.findAllByOrderByOtherPlaceIdDesc(PageRequest.of(0, limit));

        // Place + OtherPlace 데이터를 하나로 합칠 리스트
        List<UnifiedPlaceDto> merged = new ArrayList<>();

        // Place → UnifiedPlaceDto 변환 후 merged에 추가
        merged.addAll(
                placeList.stream()
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
                                    .lat(p.getLat())
                                    .lng(p.getLng())
                                    .build();
                            return dto;
                        })
                        .collect(Collectors.toList())
        );

        // OtherPlace → UnifiedPlaceDto 변환 후 merged에 추가
        merged.addAll(
        	    otherPlaceList.stream()
        	            .map(other -> {
        	                UnifiedPlaceDto dto = UnifiedPlaceDto.builder()
        	                        .id(other.getOtherPlaceId())
        	                        .name(other.getName())
        	                        .basicAddress(other.getBasicAddress())
        	                        .img1(other.getImg1())
        	                        .tag1(other.getTag1())
        	                        .tag2(other.getTag2())
        	                        .tag3(other.getTag3())
        	                        .tag4(other.getTag4())
        	                        .tag5(other.getTag5())
        	                        .likeCount((int) otherPlaceLikeRepository.countByOtherPlace(other))
        	                        .type("OTHER")
        	                        .lat(other.getLat())
        	                        .lng(other.getLng())
        	                        .build();
        	                return dto;
        	            })
        	            .collect(Collectors.toList())
        	);

        // 두 테이블 합친 리스트를 PK 기준 내림차순 + 외부 우선 정렬
        merged.sort(
                Comparator
                        .comparing(UnifiedPlaceDto::getId, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1)
        );

        // limit 개수만큼 잘라서 반환
        return merged.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
	
	// 홈화면 거리순 장소 리스트 조회 (Place + OtherPlace)
	 @Override
	    public List<UnifiedPlaceDto> getPlacesByDistance(Double lat, Double lng, int limit) throws Exception {

	        // 외부 장소 먼저 거리순 조회
	        List<UnifiedPlaceDto> otherDtos = getOtherPlacesByDistance(lat, lng, limit);

	        int remain = limit - otherDtos.size();

	        List<UnifiedPlaceDto> placeDtos = new ArrayList<>();

	        if (remain > 0) {
	        	// 내 장소를 limit에서 남은 만큼 조회
	            List<Place> places =
	                    placeRepository.findAll(PageRequest.of(0, remain)).getContent();

	            placeDtos = places.stream()
	                    .map(place -> {
	                        double distance = (place.getLat() != null && place.getLng() != null)
	                                ? DistanceUtil.calculateDistanceKm(lat, lng, place.getLat(), place.getLng())
	                                : Double.MAX_VALUE;

	                        distance = Math.round(distance * 100.0) / 100.0;

	                        return UnifiedPlaceDto.builder()
	                                .id(place.getPlaceId())
	                                .name(place.getName())
	                                .basicAddress(place.getBasicAddress())
	                                .lat(place.getLat())
	                                .lng(place.getLng())
	                                .img1(place.getImg1())
	                                .tag1(place.getTag1())
        	                        .tag2(place.getTag2())
        	                        .tag3(place.getTag3())
        	                        .tag4(place.getTag4())
        	                        .tag5(place.getTag5())
	                                .type("PLACE")
	                                .likeCount((int) placeLikeRepository.countByPlace(place))
	                                .distanceKm(distance)
	                                .build();
	                    })
	                    .sorted(Comparator.comparing(UnifiedPlaceDto::getDistanceKm))
	                    .collect(Collectors.toList());
	        }

	        // 합치기
	        List<UnifiedPlaceDto> merged = new ArrayList<>();
	        merged.addAll(otherDtos);
	        merged.addAll(placeDtos);

	        // 최종 정렬: 외부 우선 → 거리순
	        merged.sort(
	                Comparator
	                        .comparing((UnifiedPlaceDto dto) -> "OTHER".equals(dto.getType()) ? 0 : 1)
	                        .thenComparing(UnifiedPlaceDto::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder()))
	        );

	        return merged.stream()
	                .limit(limit)
	                .collect(Collectors.toList());
	    }

	 // 외부 장소 최신순 가져오기
    @Override
    public List<UnifiedPlaceDto> getLatestOtherPlaces(int limit) throws Exception {
        List<OtherPlace> otherPlaces =
                otherPlaceRepository.findAllByOrderByOtherPlaceIdDesc(PageRequest.of(0, limit));

        return otherPlaces.stream()
                .map(other -> UnifiedPlaceDto.builder()
                        .id(other.getOtherPlaceId())
                        .name(other.getName())
                        .basicAddress(other.getBasicAddress())
                        .lat(other.getLat())
                        .lng(other.getLng())
                        .img1(other.getImg1())
                        .tag1(other.getTag1())
                        .tag2(other.getTag2())
                        .tag3(other.getTag3())
                        .tag4(other.getTag4())
                        .tag5(other.getTag5())
                        .type("OTHER")
                        .likeCount((int) otherPlaceLikeRepository.countByOtherPlace(other))
                        .build())
                .collect(Collectors.toList());
    }

    // 외부 장소 거리순 가져오기
    @Override
    public List<UnifiedPlaceDto> getOtherPlacesByDistance(Double lat, Double lng, int limit) throws Exception {
        List<OtherPlace> otherPlaces =
                otherPlaceRepository.findAll(PageRequest.of(0, limit)).getContent();

        return otherPlaces.stream()
                .map(other -> {
                    double distance = (other.getLat() != null && other.getLng() != null)
                            ? DistanceUtil.calculateDistanceKm(lat, lng, other.getLat(), other.getLng())
                            : Double.MAX_VALUE;

                    distance = Math.round(distance * 100.0) / 100.0;

                    return UnifiedPlaceDto.builder()
                            .id(other.getOtherPlaceId())
                            .name(other.getName())
                            .basicAddress(other.getBasicAddress())
                            .lat(other.getLat())
                            .lng(other.getLng())
                            .img1(other.getImg1())
                            .tag1(other.getTag1())
                            .tag2(other.getTag2())
                            .tag3(other.getTag3())
                            .tag4(other.getTag4())
                            .tag5(other.getTag5())
                            .type("OTHER")
                            .likeCount((int) otherPlaceLikeRepository.countByOtherPlace(other))
                            .distanceKm(distance)
                            .build();
                })
                .sorted(Comparator.comparing(UnifiedPlaceDto::getDistanceKm))
                .limit(limit)
                .collect(Collectors.toList());
    }
}


