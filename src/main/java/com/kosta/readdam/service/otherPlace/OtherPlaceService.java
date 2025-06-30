package com.kosta.readdam.service.otherPlace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;

public interface OtherPlaceService {
	void save(OtherPlaceDto dto);
	OtherPlaceDto getOtherPlaceDetail(Integer id);
	Page<OtherPlaceSummaryDto> getOtherPlaceList(Pageable pageable, String keyword, String filterBy);
	void updateOtherPlace(Integer id, OtherPlaceDto dto);
}
