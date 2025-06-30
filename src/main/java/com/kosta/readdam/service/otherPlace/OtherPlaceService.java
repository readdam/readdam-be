package com.kosta.readdam.service.otherPlace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;

public interface OtherPlaceService {
	void save(OtherPlaceDto dto);
	List<OtherPlaceDto> getAllPlaces();  // 안쓰는건가?
//	void update(Long id, OtherPlaceDto dto);
	OtherPlaceDto getOtherPlaceDetail(Integer id);
	Page<OtherPlaceSummaryDto> getOtherPlaceList(Pageable pageable, String keyword, String filterBy);
//	void updateOtherPlace(Integer placeId, OtherPlaceDto placeDto, List<MultipartFile> newImages, List<String> existingImages, String keywordsJson);
	void updateOtherPlace(Integer id, OtherPlaceDto dto);
}
