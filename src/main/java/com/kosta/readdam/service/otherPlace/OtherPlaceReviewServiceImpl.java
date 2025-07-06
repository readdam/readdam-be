package com.kosta.readdam.service.otherPlace;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.OtherPlaceReviewDto;
import com.kosta.readdam.dto.place.PlaceReviewRequest;
import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceReview;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtherPlaceReviewServiceImpl implements OtherPlaceReviewService {

	private final OtherPlaceReviewRepository otherPlaceReviewRepository;
    private final OtherPlaceRepository otherPlaceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OtherPlaceReviewDto writeReview(String username, PlaceReviewRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        OtherPlace otherPlace = otherPlaceRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("외부 장소를 찾을 수 없습니다."));

        OtherPlaceReview review = OtherPlaceReview.builder()
                .user(user)
                .otherPlace(otherPlace)
                .content(request.getContent())
                .rating(request.getRating())
                .isHide(false)
                .regTime(LocalDateTime.now())
                .build();

        otherPlaceReviewRepository.save(review);

        return review.toDto();
    }

    @Override
    public Page<OtherPlaceReviewDto> getReviews(Integer otherPlaceId, String username, int page, int size) {
        Page<OtherPlaceReview> reviews = otherPlaceReviewRepository.findByOtherPlace_OtherPlaceIdOrderByRegTimeDesc(
                otherPlaceId, PageRequest.of(page, size)
        );
        return reviews.map(OtherPlaceReview::toDto);
    }

    @Override
    @Transactional
    public void updateReview(Integer id, String username, String content, Integer rating, Boolean isHide) {
        OtherPlaceReview review = otherPlaceReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }
        review.setContent(content);
        review.setRating(rating);
        review.setIsHide(isHide);
    }

    @Override
    @Transactional
    public void deleteReview(Integer id, String username) {
        OtherPlaceReview review = otherPlaceReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        otherPlaceReviewRepository.delete(review);
    }

}
