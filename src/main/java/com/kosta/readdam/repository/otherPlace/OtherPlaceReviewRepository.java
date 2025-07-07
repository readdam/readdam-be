package com.kosta.readdam.repository.otherPlace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.OtherPlaceReview;

public interface OtherPlaceReviewRepository extends JpaRepository<OtherPlaceReview, Integer>{
	Page<OtherPlaceReview> findByOtherPlace_OtherPlaceIdOrderByRegTimeDesc(Integer otherPlaceId, Pageable pageable);
}
