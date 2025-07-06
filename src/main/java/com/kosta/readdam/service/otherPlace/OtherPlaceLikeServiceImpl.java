package com.kosta.readdam.service.otherPlace;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceLikeRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtherPlaceLikeServiceImpl implements OtherPlaceLikeService {

	private final OtherPlaceLikeRepository otherPlaceLikeRepository;
    private final UserRepository userRepository;
    private final OtherPlaceRepository otherPlaceRepository;

    @Override
    @Transactional
    public boolean toggleLike(String username, Integer otherPlaceId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        OtherPlace otherPlace = otherPlaceRepository.findById(otherPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("외부 장소를 찾을 수 없습니다."));

        return otherPlaceLikeRepository.findByUserAndOtherPlace(user, otherPlace)
                .map(like -> {
                    otherPlaceLikeRepository.delete(like);
                    return false;
                })
                .orElseGet(() -> {
                    OtherPlaceLike newLike = OtherPlaceLike.builder()
                            .user(user)
                            .otherPlace(otherPlace)
                            .date(LocalDateTime.now())
                            .build();
                    otherPlaceLikeRepository.save(newLike);
                    return true;
                });
    }

    @Override
    public boolean isLiked(String username, Integer otherPlaceId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        OtherPlace otherPlace = otherPlaceRepository.findById(otherPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("외부 장소를 찾을 수 없습니다."));

        return otherPlaceLikeRepository.existsByUserAndOtherPlace(user, otherPlace);
    }

    @Override
    public Integer countLikes(Integer otherPlaceId) {
        OtherPlace otherPlace = otherPlaceRepository.findById(otherPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("외부 장소를 찾을 수 없습니다."));
        return otherPlaceLikeRepository.countByOtherPlace(otherPlace);
    }

}
