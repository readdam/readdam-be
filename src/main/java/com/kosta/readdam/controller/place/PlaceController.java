package com.kosta.readdam.controller.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.dto.place.UnifiedPlacePageResponse;
import com.kosta.readdam.service.otherPlace.OtherPlaceService;
import com.kosta.readdam.service.place.PlaceService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;
import static com.kosta.readdam.util.DistanceUtil.calculateDistanceKm;


@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;
    private final OtherPlaceService otherPlaceService;

    @GetMapping("/all")
    public List<UnifiedPlaceDto> getAllPlaces() {
        List<UnifiedPlaceDto> placeList = placeService.getUnifiedList();
        List<UnifiedPlaceDto> otherPlaceList = otherPlaceService.getUnifiedList();
        List<UnifiedPlaceDto> merged = new ArrayList<>();
        merged.addAll(placeList);
        merged.addAll(otherPlaceList);
        return merged;
    }
    
    
    @GetMapping("/search")
    public UnifiedPlacePageResponse searchPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "ALL") String placeType,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(defaultValue = "latest") String sortBy
    ) {
        // 가져올 갯수 넉넉히
        int fetchSize = size * 5;

        List<UnifiedPlaceDto> placeList = new ArrayList<>();
        List<UnifiedPlaceDto> otherPlaceList = new ArrayList<>();

        if (!"OTHER".equalsIgnoreCase(placeType)) {
            placeList = placeService.searchPlaces(tag, keyword, lat, lng, radiusKm, 0, fetchSize, sortBy);
        }
        if (!"PLACE".equalsIgnoreCase(placeType)) {
            otherPlaceList = otherPlaceService.searchPlaces(tag, keyword, lat, lng, radiusKm, 0, fetchSize, sortBy);
        }

        // 합치기
        List<UnifiedPlaceDto> merged = new ArrayList<>();
        merged.addAll(placeList);
        merged.addAll(otherPlaceList);

        // 정렬
        // === 거리순 처리 ===
        if ("distance".equalsIgnoreCase(sortBy)) {
            if (lat == null || lng == null) {
                throw new IllegalArgumentException("거리순 정렬에는 lat/lng가 필요합니다.");
            }

            // 거리 계산
            for (UnifiedPlaceDto dto : merged) {
                if (dto.getLat() != null && dto.getLng() != null) {
                    dto.setDistanceKm(calculateDistanceKm(lat, lng, dto.getLat(), dto.getLng()));
                } else {
                    dto.setDistanceKm(Double.MAX_VALUE);
                }
            }

            // 거리순 + 외부 우선
            merged.sort(
                Comparator
                    .comparing(UnifiedPlaceDto::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1)
            );
        }

        // === 인기순 처리 ===
        else if ("likes".equalsIgnoreCase(sortBy)) {
            merged.sort(
                Comparator
                    .comparing(UnifiedPlaceDto::getLikeCount, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1)
            );
        }

        // === 최신순 처리 (id DESC)
        else {
            merged.sort(
                Comparator
                    .comparing(UnifiedPlaceDto::getId, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1)
            );
        }


        // 자바에서 페이징
        int totalElements = merged.size();
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        List<UnifiedPlaceDto> pagedContent = (start >= totalElements)
                ? Collections.emptyList()
                : merged.subList(start, end);

        PageInfo2 pageInfo = new PageInfo2(
                page,
                size,
                end == totalElements,
                totalElements,
                (int) Math.ceil((double) totalElements / size),
                end < totalElements
        );

        return new UnifiedPlacePageResponse(pagedContent, pageInfo);
    }

}
