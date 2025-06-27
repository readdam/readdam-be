package com.kosta.readdam.repository.otherPlace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.entity.OtherPlace;

public interface OtherPlaceRepositoryCustom {
	Page<OtherPlace> findAllByFilter(Pageable pageable, String keyword, String filterBy);
}
