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
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtherPlaceServiceImpl implements OtherPlaceService {

	private final OtherPlaceRepository otherPlaceRepository;
	
	@Override
	@Transactional
	public void save(OtherPlaceDto dto) {
	    OtherPlace entity = dto.toEntity();
	    otherPlaceRepository.save(entity);
	}

	@Override
    public List<OtherPlaceDto> getAllPlaces() {
        return otherPlaceRepository.findAll().stream()
                .map(OtherPlaceDto::fromEntity)
                .collect(Collectors.toList());
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

}
