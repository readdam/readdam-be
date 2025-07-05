package com.kosta.readdam.repository.place;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.PlaceReview;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Integer>{
	Page<PlaceReview> findByPlace_PlaceIdOrderByRegTimeDesc(Integer placeId, Pageable pageable);
}
