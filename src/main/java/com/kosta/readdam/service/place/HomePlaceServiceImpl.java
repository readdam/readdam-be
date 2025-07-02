package com.kosta.readdam.service.place;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.place.HomePlaceSummaryDto;
import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;
import com.kosta.readdam.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class HomePlaceServiceImpl implements HomePlaceService {
	
    private final PlaceRepository placeRepository;
    private final OtherPlaceRepository otherPlaceRepository;

	@Override
	public List<HomePlaceSummaryDto> getLatestPlaces(int limit) throws Exception {
        
        // Place 테이블에서 최신순 (PK 내림차순)으로 limit 만큼 조회
        List<Place> placeList = placeRepository.findAllByOrderByPlaceIdDesc(PageRequest.of(0, limit));

        // OtherPlace 테이블에서 최신순 (PK 내림차순)으로 limit 만큼 조회
        List<OtherPlace> otherPlaceList = otherPlaceRepository.findAllByOrderByOtherPlaceIdDesc(PageRequest.of(0, limit));

        // Place + OtherPlace 데이터를 하나로 합칠 리스트
        List<HomePlaceSummaryDto> merged = new ArrayList<>();

        // Place → HomePlaceSummaryDto 변환 후 merged에 추가
        merged.addAll(
            placeList.stream()
                    .map(place -> HomePlaceSummaryDto.builder()
                            .id(place.getPlaceId())
                            .name(place.getName())
                            .address(place.getBasicAddress()) 
                            .type("PLACE")
                            .build())
                    .collect(Collectors.toList())
        );

        // OtherPlace → HomePlaceSummaryDto 변환 후 merged에 추가
        merged.addAll(
            otherPlaceList.stream()
                    .map(other -> HomePlaceSummaryDto.builder()
                            .id(other.getOtherPlaceId())
                            .name(other.getName())
                            .address(other.getBasicAddress())
                            .type("OTHER_PLACE")
                            .build())
                    .collect(Collectors.toList())
        );

        // 두 테이블 합친 리스트를 PK 기준 내림차순 정렬
        merged.sort(Comparator.comparing(HomePlaceSummaryDto::getId).reversed());

        // limit 개수만큼 잘라서 반환
        return merged.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
	

	@Override
	public List<HomePlaceSummaryDto> getPlacesByDistance(Double lat, Double lng, int limit) throws Exception {
        // 거리순 구현 예정
		throw new UnsupportedOperationException("거리순 정렬은 아직 구현되지 않았습니다.");
    }

}
