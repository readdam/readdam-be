package com.kosta.readdam.service.place;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.PlaceReviewDto;
import com.kosta.readdam.dto.place.PlaceReviewRequest;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceReview;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.place.PlaceRepository;
import com.kosta.readdam.repository.place.PlaceReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceReviewServiceImpl implements PlaceReviewService {
	
	private final PlaceReviewRepository placeReviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public PlaceReviewDto writeReview(String username, PlaceReviewRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        PlaceReview review = PlaceReview.builder()
                .content(request.getContent())
                .rating(request.getRating())
                .isHide(request.getIsHide())
                .user(user)
                .place(place)
                .regTime(LocalDateTime.now())
                .build();

        PlaceReview saved = placeReviewRepository.save(review);

        return saved.toDto();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceReviewDto> getReviews(Integer placeId, String username, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return placeReviewRepository.findByPlace_PlaceIdOrderByRegTimeDesc(placeId, pageRequest)
                .map(PlaceReview::toDto);
    }
}
