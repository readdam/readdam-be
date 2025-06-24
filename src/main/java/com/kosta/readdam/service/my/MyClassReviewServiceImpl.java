package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.ClassReviewDto;
import com.kosta.readdam.entity.ClassReview;
import com.kosta.readdam.repository.ClassReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyClassReviewServiceImpl implements MyClassReviewService{

	
	private final ClassReviewRepository reviewRepository;

    @Override
    public List<ClassReviewDto> getMyReviews(String username) throws Exception {
        return reviewRepository.findByUser_Username(username).stream()
                .map(ClassReview::toDto)
                .collect(Collectors.toList());
    }
    
}
