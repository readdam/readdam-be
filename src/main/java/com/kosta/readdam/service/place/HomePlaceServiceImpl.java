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

        List<Place> placeList = placeRepository.findAllByOrderByPlaceIdDesc(PageRequest.of(0, limit));
        List<OtherPlace> otherPlaceList = otherPlaceRepository.findAllByOrderByOtherPlaceIdDesc(PageRequest.of(0, limit));

        List<UnifiedPlaceDto> merged = new ArrayList<>();
        merged.addAll(convertPlaceList(placeList));
        merged.addAll(convertOtherPlaceList(otherPlaceList));

        merged = removeDuplicates(merged);

        merged.sort(
                Comparator.comparing(UnifiedPlaceDto::getId, Comparator.nullsLast(Comparator.reverseOrder()))
                          .thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1)
        );

        return merged.stream()
                     .limit(limit)
                     .collect(Collectors.toList());
    }

    // 홈화면 거리순 장소 리스트 조회 (Place + OtherPlace)
    @Override
    public List<UnifiedPlaceDto> getPlacesByDistance(Double lat, Double lng, int limit) throws Exception {
        int fetchSize = limit * 5;

        List<UnifiedPlaceDto> otherDtos = convertOtherPlaceList(
                otherPlaceRepository.findAll(PageRequest.of(0, fetchSize)).getContent()
        );
        List<UnifiedPlaceDto> placeDtos = convertPlaceList(
                placeRepository.findAll(PageRequest.of(0, fetchSize)).getContent()
        );

        // 거리 계산
        for (UnifiedPlaceDto dto : otherDtos) {
            calculateDistance(lat, lng, dto);
        }
        for (UnifiedPlaceDto dto : placeDtos) {
            calculateDistance(lat, lng, dto);
        }

        List<UnifiedPlaceDto> merged = new ArrayList<>();
        merged.addAll(otherDtos);
        merged.addAll(placeDtos);

        merged = removeDuplicates(merged);

        merged.sort(
                Comparator.comparing(UnifiedPlaceDto::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder()))
                          .thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1)
        );

        return merged.stream()
                     .limit(limit)
                     .collect(Collectors.toList());
    }

    //Place → UnifiedPlaceDto 변환
    private List<UnifiedPlaceDto> convertPlaceList(List<Place> places) {
        return places.stream()
                .map(p -> UnifiedPlaceDto.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    //OtherPlace → UnifiedPlaceDto 변환
    private List<UnifiedPlaceDto> convertOtherPlaceList(List<OtherPlace> others) {
        return others.stream()
                .map(o -> UnifiedPlaceDto.builder()
                        .id(o.getOtherPlaceId())
                        .name(o.getName())
                        .basicAddress(o.getBasicAddress())
                        .img1(o.getImg1())
                        .tag1(o.getTag1())
                        .tag2(o.getTag2())
                        .tag3(o.getTag3())
                        .tag4(o.getTag4())
                        .tag5(o.getTag5())
                        .likeCount((int) otherPlaceLikeRepository.countByOtherPlace(o))
                        .type("OTHER")
                        .lat(o.getLat())
                        .lng(o.getLng())
                        .build())
                .collect(Collectors.toList());
    }

    //거리 계산 후 dto 업데이트
    private void calculateDistance(Double lat, Double lng, UnifiedPlaceDto dto) {
        if (dto.getLat() != null && dto.getLng() != null) {
            double distance = DistanceUtil.calculateDistanceKm(lat, lng, dto.getLat(), dto.getLng());
            dto.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        } else {
            dto.setDistanceKm(Double.MAX_VALUE);
        }
    }

    //중복 제거 (타입+ID 기준)
    private List<UnifiedPlaceDto> removeDuplicates(List<UnifiedPlaceDto> list) {
        return new ArrayList<>(
                list.stream()
                        .collect(Collectors.toMap(
                                dto -> dto.getType() + "-" + dto.getId(),
                                dto -> dto,
                                (dto1, dto2) -> dto1
                        ))
                        .values()
        );
    }
}
