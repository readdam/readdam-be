package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.place.PlaceLikeRepository;
import com.kosta.readdam.repository.place.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPlaceLikeServiceImpl implements MyPlaceLikeService {
	
    private final PlaceLikeRepository placeLikeRepo;
    private final PlaceRepository placeRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public List<PlaceDto> getLikedPlaces(String username) throws Exception {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        
        return placeLikeRepo.findByUser(user).stream()
            .map(pl -> {
                Place p = pl.getPlace();
                long count = placeLikeRepo.countByPlace(p);
                return PlaceDto.builder()
                    .placeId(p.getPlaceId())
                    .name(p.getName())
                    .basicAddress(p.getBasicAddress())
                    .detailAddress(p.getDetailAddress())
                    // tag1~tag3만 설정
                    .tag1(p.getTag1())
                    .tag2(p.getTag2())
                    .tag3(p.getTag3())
                    .img1(p.getImg1())
                    .likeCount(count)
                    .liked(true)
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlaceDto toggleLike(String username, Integer placeId) throws Exception {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        Place place = placeRepo.findById(placeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소"));

        // 좋아요 토글
        placeLikeRepo.findByUserAndPlace(user, place)
            .ifPresentOrElse(
                pl -> placeLikeRepo.delete(pl),
                () -> placeLikeRepo.save(
                    PlaceLike.builder()
                        .user(user)
                        .place(place)
                        .date(LocalDateTime.now())
                        .build()
                )
            );

        long count = placeLikeRepo.countByPlace(place);
        boolean liked = placeLikeRepo.findByUserAndPlace(user, place).isPresent();

        return PlaceDto.builder()
            .placeId(place.getPlaceId())
            .name(place.getName())
            .basicAddress(place.getBasicAddress())
            .detailAddress(place.getDetailAddress())
            .tag1(place.getTag1())
            .tag2(place.getTag2())
            .tag3(place.getTag3())
            .img1(place.getImg1())
            .likeCount(count)
            .liked(liked)
            .build();
    }
}
