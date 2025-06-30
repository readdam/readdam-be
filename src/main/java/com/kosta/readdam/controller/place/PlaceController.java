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
    
//    @GetMapping("/search")
//    public UnifiedPlacePageResponse searchPlaces(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "12") int size,
//            @RequestParam(required = false) String tag,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(defaultValue = "ALL") String placeType, // PLACE, OTHER, ALL
//            @RequestParam(required = false) Double lat,
//            @RequestParam(required = false) Double lng,
//            @RequestParam(required = false) Double radiusKm,
//            @RequestParam(defaultValue = "latest") String sortBy
//    ) {
//        // Step 1: 각 서비스에 조건 전달
//        List<UnifiedPlaceDto> placeList = new ArrayList<>();
//        List<UnifiedPlaceDto> otherPlaceList = new ArrayList<>();
//
//        if (!"OTHER".equalsIgnoreCase(placeType)) {
//            placeList = placeService.searchPlaces(tag, keyword, lat, lng, radiusKm);
//        }
//
//        if (!"PLACE".equalsIgnoreCase(placeType)) {
//            otherPlaceList = otherPlaceService.searchPlaces(tag, keyword, lat, lng, radiusKm);
//        }
//        
//        
//
//        // Step 2: 결과 합치기
//        List<UnifiedPlaceDto> merged = new ArrayList<>();
//        merged.addAll(placeList);
//        merged.addAll(otherPlaceList);
//
//        // Step 3: 거리순 정렬 (예시)
////        merged.sort(Comparator.comparing(UnifiedPlaceDto::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder())));
//        
//        // Step 3: 정렬
//        if ("likes".equalsIgnoreCase(sortBy)) {
//            merged.sort(Comparator.comparing(
//                    UnifiedPlaceDto::getLikeCount,
//                    Comparator.nullsLast(Comparator.reverseOrder())
//            ));
//        } else if ("distance".equalsIgnoreCase(sortBy)) {
//            merged.sort(Comparator.comparing(
//                    UnifiedPlaceDto::getDistanceKm,
//                    Comparator.nullsLast(Comparator.naturalOrder())
//            ));
//        } else { // 기본 최신순 (id 내림차순)
//            merged.sort(Comparator.comparing(
//                    UnifiedPlaceDto::getId,
//                    Comparator.nullsLast(Comparator.reverseOrder())
//            ));
//        }
//
//        // Step 4: 페이징 처리
//        int totalElements = merged.size();
//        int start = page * size;
//        int end = Math.min(start + size, totalElements);
//
//        List<UnifiedPlaceDto> pagedContent = (start >= totalElements)
//                ? Collections.emptyList()
//                : merged.subList(start, end);
//
//        // Step 5: 페이지 정보 생성
//        PageInfo2 pageInfo = new PageInfo2(
//                page + 1,
//                size,
//                end == totalElements,
//                totalElements,
//                (int) Math.ceil((double) totalElements / size),
//                end < totalElements
//        );
//
//        return new UnifiedPlacePageResponse(pagedContent, pageInfo);
//    }
    
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
            @RequestParam(defaultValue = "likes") String sortBy
    ) {
        // 가져올 갯수 넉넉히
        int fetchSize = size * 3;

        List<UnifiedPlaceDto> placeList = new ArrayList<>();
        List<UnifiedPlaceDto> otherPlaceList = new ArrayList<>();

//        if (!"OTHER".equalsIgnoreCase(placeType)) {
//            placeList = placeService.searchPlaces(tag, keyword, lat, lng, radiusKm, fetchSize);
//        }
//        if (!"PLACE".equalsIgnoreCase(placeType)) {
//            otherPlaceList = otherPlaceService.searchPlaces(tag, keyword, lat, lng, radiusKm, fetchSize);
//        }

        if (!"OTHER".equalsIgnoreCase(placeType)) {
            placeList = placeService.searchPlaces(tag, keyword, lat, lng, radiusKm, 0, fetchSize);
        }
        if (!"PLACE".equalsIgnoreCase(placeType)) {
            otherPlaceList = otherPlaceService.searchPlaces(tag, keyword, lat, lng, radiusKm, 0, fetchSize);
        }

        
        // 합치기
        List<UnifiedPlaceDto> merged = new ArrayList<>();
        merged.addAll(placeList);
        merged.addAll(otherPlaceList);

        // 
//        정렬: 좋아요순 + 동점 시 외부 우선
        merged.sort((a, b) -> {
            int compareLikes = Integer.compare(b.getLikeCount(), a.getLikeCount());
            if (compareLikes != 0) return compareLikes;

            // 좋아요 동점이면 외부(OTHER)가 우선
            if ("OTHER".equals(b.getType()) && !"OTHER".equals(a.getType())) {
                return -1;
            } else if (!"OTHER".equals(b.getType()) && "OTHER".equals(a.getType())) {
                return 1;
            } else {
                return 0;
            }
        });

        // 최종 페이지 처리
        int totalElements = merged.size();
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        List<UnifiedPlaceDto> pagedContent = (start >= totalElements)
                ? Collections.emptyList()
                : merged.subList(start, end);

        PageInfo2 pageInfo = new PageInfo2(
                page + 1,
                size,
                end == totalElements,
                totalElements,
                (int) Math.ceil((double) totalElements / size),
                end < totalElements
        );

        return new UnifiedPlacePageResponse(pagedContent, pageInfo);
    }

}
