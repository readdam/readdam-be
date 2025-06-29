package com.kosta.readdam.service.otherPlace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;

public interface OtherPlaceService {
	void save(OtherPlaceDto dto);
	List<OtherPlaceDto> getAllPlaces();
	Page<OtherPlaceSummaryDto> getOtherPlaceList(Pageable pageable, String keyword, String filterBy);	
}
