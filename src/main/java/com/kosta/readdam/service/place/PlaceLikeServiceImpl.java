package com.kosta.readdam.service.place;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.place.PlaceLikeRepository;
import com.kosta.readdam.repository.place.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceLikeServiceImpl implements PlaceLikeService {

	private final PlaceLikeRepository placeLikeRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public boolean toggleLike(String username, Integer placeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        return placeLikeRepository.findByUserAndPlace(user, place)
                .map(existing -> {
                    placeLikeRepository.delete(existing);
                    return false; // 좋아요 취소됨
                })
                .orElseGet(() -> {
                    PlaceLike like = PlaceLike.builder()
                            .user(user)
                            .place(place)
                            .date(LocalDateTime.now())
                            .build();
                    placeLikeRepository.save(like);
                    return true; // 좋아요 생성됨
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(String username, Integer placeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        return placeLikeRepository.findByUserAndPlace(user, place).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikes(Integer placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        return placeLikeRepository.countByPlace(place);
    }

}
