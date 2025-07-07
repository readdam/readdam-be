package com.kosta.readdam.repository.otherPlace;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceLike;
import com.kosta.readdam.entity.User;

public interface OtherPlaceRepository extends JpaRepository<OtherPlace, Integer>, OtherPlaceRepositoryCustom {
	List<OtherPlace> findAllByOrderByOtherPlaceIdDesc(Pageable pageable);// home 최신순 내림차순 + limit 4개 조회용
}
