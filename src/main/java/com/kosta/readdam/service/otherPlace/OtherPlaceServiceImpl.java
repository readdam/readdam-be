package com.kosta.readdam.service.otherPlace;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.SearchResultDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.repository.otherPlace.OtherPlaceLikeRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtherPlaceServiceImpl implements OtherPlaceService {

	private final OtherPlaceRepository otherPlaceRepository;
    private final OtherPlaceLikeRepository otherPlaceLikeRepository;
    
	@Override
	@Transactional
	public void save(OtherPlaceDto dto) {
	    OtherPlace entity = dto.toEntity();
	    otherPlaceRepository.save(entity);
	}
	
	@Override
	public Page<OtherPlaceSummaryDto> getOtherPlaceList(Pageable pageable, String keyword, String filterBy) {
		Page<OtherPlaceSummaryDto> page = otherPlaceRepository.findAllByFilter(pageable, keyword, filterBy);

	    // tags 리스트 조립
	    page.getContent().forEach(dto -> {
	        dto.setTags(
	            Stream.of(dto.getTag1(), dto.getTag2(), dto.getTag3(), dto.getTag4(), dto.getTag5())
	                .filter(Objects::nonNull)
	                .filter(s -> !s.isBlank())
	                .collect(Collectors.toList())
	        );
	    });

	    return page;
	}
	
	public OtherPlaceDto getOtherPlaceDetail(Integer id) {
	    OtherPlace entity = otherPlaceRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("해당 장소 없음"));

	    return entity.toDto();
	}

	@Transactional
	public void updateOtherPlace(Integer id, OtherPlaceDto dto) {
	    OtherPlace entity = otherPlaceRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 장소 없음"));
	    BeanUtils.copyProperties(dto, entity, "otherPlaceId");
	}

	public List<UnifiedPlaceDto> getUnifiedList() {
        List<OtherPlace> otherPlaces = otherPlaceRepository.findAll();

        return otherPlaces.stream()
                .map(o -> {
                    UnifiedPlaceDto dto = UnifiedPlaceDto.builder()
                            .id(o.getOtherPlaceId())
                            .name(o.getName())
                            .basicAddress(o.getBasicAddress())
                            .img1(o.getImg1())
                            .tag1(o.getTag1())
                            .tag2(o.getTag2())
                            .tag3(o.getTag3())
                            .tag4(o.getTag4())
                            .tag5(o.getTag5())
                            .likeCount(otherPlaceLikeRepository.countByOtherPlace(o))
                            .type("OTHER")
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }
	
//	public List<UnifiedPlaceDto> searchPlaces(
//            String tag,
//            String keyword,
//            Double userLat,
//            Double userLng,
//            Double radiusKm
//    ) {
//        List<OtherPlace> places = otherPlaceRepository.findAll();
//
//        return places.stream()
//                .map(p -> {
//                    // 거리 계산 (nullable)
//                    Double distance = null;
//                    if (userLat != null && userLng != null) {
//                        distance = DistanceUtil.calculateDistanceKm(userLat, userLng, p.getLat(), p.getLng());
//                    }
//
//                    return UnifiedPlaceDto.builder()
//                            .id(p.getOtherPlaceId().longValue())
//                            .name(p.getName())
//                            .basicAddress(p.getBasicAddress())
//                            .img1(p.getImg1())
//                            .tag1(p.getTag1())
//                            .tag2(p.getTag2())
//                            .tag3(p.getTag3())
//                            .tag4(p.getTag4())
//                            .tag5(p.getTag5())
//                            .likeCount((int) otherPlaceLikeRepository.countByOtherPlace(p))
//                            .type("OTHER")
//                            .distanceKm(distance)
//                            .build();
//                })
//                // 태그 필터
//                .filter(dto -> {
//                    if (tag == null || tag.isEmpty()) return true;
//                    return tag.equalsIgnoreCase(dto.getTag1())
//                            || tag.equalsIgnoreCase(dto.getTag2())
//                            || tag.equalsIgnoreCase(dto.getTag3())
//                            || tag.equalsIgnoreCase(dto.getTag4())
//                            || tag.equalsIgnoreCase(dto.getTag5());
//                })
//                // 검색어 필터
//                .filter(dto -> {
//                    if (keyword == null || keyword.isEmpty()) return true;
//                    return dto.getName().toLowerCase().contains(keyword.toLowerCase())
//                            || dto.getBasicAddress().toLowerCase().contains(keyword.toLowerCase());
//                })
//                // 거리 필터
//                .filter(dto -> {
//                    if (radiusKm == null || dto.getDistanceKm() == null) return true;
//                    return dto.getDistanceKm() <= radiusKm;
//                })
//                .collect(Collectors.toList());
//    }
	
	@Override
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
        return otherPlaceRepository.searchPlaces(
            tag, keyword, lat, lng, radiusKm, offset, limit, sortBy
        );
    }

	@Override
	public SearchResultDto<PlaceDto> searchForAll(String keyword, String sort, int limit) {
		return otherPlaceRepository.searchForAll(keyword, sort, limit);
	}
}
