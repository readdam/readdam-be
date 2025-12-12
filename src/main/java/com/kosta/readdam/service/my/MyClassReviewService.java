package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.ClassReviewDto;

public interface MyClassReviewService {

	List<ClassReviewDto> getMyReviews(String username) throws Exception;

}
