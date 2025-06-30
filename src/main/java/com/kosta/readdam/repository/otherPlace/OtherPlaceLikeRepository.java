package com.kosta.readdam.repository.otherPlace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceLike;

public interface OtherPlaceLikeRepository extends JpaRepository<OtherPlaceLike, Integer> {
	Integer countByOtherPlace(OtherPlace otherPlace);
}
