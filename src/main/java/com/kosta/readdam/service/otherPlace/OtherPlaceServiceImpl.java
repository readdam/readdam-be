package com.kosta.readdam.service.otherPlace;

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


}
