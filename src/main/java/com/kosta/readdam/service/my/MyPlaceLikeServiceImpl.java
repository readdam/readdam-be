package com.kosta.readdam.service.my;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceLike;
import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceLikeRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceRepository;
import com.kosta.readdam.repository.place.PlaceLikeRepository;
import com.kosta.readdam.repository.place.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPlaceLikeServiceImpl implements MyPlaceLikeService {

    private final PlaceLikeRepository placeLikeRepo;
    private final PlaceRepository placeRepo;
    private final OtherPlaceLikeRepository otherPlaceLikeRepo;
    private final OtherPlaceRepository otherPlaceRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public List<UnifiedPlaceDto> getLikedPlaces(String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        List<UnifiedPlaceDto> result = new ArrayList<>();

        // 일반 Place
        for (PlaceLike like : placeLikeRepo.findByUser(user)) {
            Place p = like.getPlace();
            long count = placeLikeRepo.countByPlace(p);
            result.add(UnifiedPlaceDto.builder()
                .id(p.getPlaceId())
                .name(p.getName())
                .basicAddress(p.getBasicAddress())
                .img1(p.getImg1())
                .tag1(p.getTag1())
                .tag2(p.getTag2())
                .tag3(p.getTag3())
                .tag4(null)
                .tag5(null)
                .likeCount((int) count)
                .type("PLACE")
                .lat(p.getLat())
                .lng(p.getLng())
                .build());
        }

        // OtherPlace
        for (OtherPlaceLike like : otherPlaceLikeRepo.findByUser(user)) {
            OtherPlace p = like.getOtherPlace();
            long count = otherPlaceLikeRepo.countByOtherPlace(p);
            result.add(UnifiedPlaceDto.builder()
                .id(p.getOtherPlaceId())
                .name(p.getName())
                .basicAddress(p.getBasicAddress())
                .img1(p.getImg1())
                .tag1(p.getTag1())
                .tag2(p.getTag2())
                .tag3(p.getTag3())
                .tag4(p.getTag4())
                .tag5(p.getTag5())
                .likeCount((int) count)
                .type("OTHER")
                .lat(p.getLat())
                .lng(p.getLng())
                .build());
        }

        return result;
    }
    
    @Override
    @Transactional
    public UnifiedPlaceDto toggleUnifiedLike(String username, Integer id, String type) throws Exception {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        if ("PLACE".equalsIgnoreCase(type)) {
            Place place = placeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소"));
            
            placeLikeRepo.findByUserAndPlace(user, place)
                .ifPresentOrElse(
                    pl -> placeLikeRepo.delete(pl),
                    () -> placeLikeRepo.save(
                        PlaceLike.builder()
                            .user(user)
                            .place(place)
                            .date(java.time.LocalDateTime.now())
                            .build()
                    )
                );

            long count = placeLikeRepo.countByPlace(place);
            boolean liked = placeLikeRepo.findByUserAndPlace(user, place).isPresent();

            return UnifiedPlaceDto.builder()
                .id(place.getPlaceId())
                .name(place.getName())
                .basicAddress(place.getBasicAddress())
                .img1(place.getImg1())
                .tag1(place.getTag1())
                .tag2(place.getTag2())
                .tag3(place.getTag3())
                .likeCount((int) count)
                .type("PLACE")
                .lat(place.getLat())
                .lng(place.getLng())
                .build();

        } else if ("OTHER".equalsIgnoreCase(type)) {
            OtherPlace otherPlace = otherPlaceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소(기타)"));

            otherPlaceLikeRepo.findByUserAndOtherPlace(user, otherPlace)
                .ifPresentOrElse(
                    pl -> otherPlaceLikeRepo.delete(pl),
                    () -> otherPlaceLikeRepo.save(
                        OtherPlaceLike.builder()
                            .user(user)
                            .otherPlace(otherPlace)
                            .date(java.time.LocalDateTime.now())
                            .build()
                    )
                );

            long count = otherPlaceLikeRepo.countByOtherPlace(otherPlace);
            boolean liked = otherPlaceLikeRepo.findByUserAndOtherPlace(user, otherPlace).isPresent();

            return UnifiedPlaceDto.builder()
                .id(otherPlace.getOtherPlaceId())
                .name(otherPlace.getName())
                .basicAddress(otherPlace.getBasicAddress())
                .img1(otherPlace.getImg1())
                .tag1(otherPlace.getTag1())
                .tag2(otherPlace.getTag2())
                .tag3(otherPlace.getTag3())
                .tag4(otherPlace.getTag4())
                .tag5(otherPlace.getTag5())
                .likeCount((int) count)
                .type("OTHER")
                .lat(otherPlace.getLat())
                .lng(otherPlace.getLng())
                .build();

        } else {
            throw new IllegalArgumentException("유효하지 않은 type 값: " + type);
        }
    }

}

