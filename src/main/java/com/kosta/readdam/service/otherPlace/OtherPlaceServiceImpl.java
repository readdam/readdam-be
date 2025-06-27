package com.kosta.readdam.service.otherPlace;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.OtherPlaceDto;
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
	public Page<OtherPlaceDto> getOtherPlaceList(Pageable pageable, String keyword, String filterBy) {
	    Page<OtherPlace> placePage = otherPlaceRepository.findAllByFilter(pageable, keyword, filterBy);
	    return placePage.map(OtherPlaceDto::fromEntity);
	}
}
