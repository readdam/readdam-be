package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceReview;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceReviewDto {

    private Integer placeReviewId;
    private String content;
    private Integer rating;
    private String username;
    private Integer placeId;
    private LocalDateTime regTime;
    private Boolean isHide;

    public PlaceReview toEntity(User user, Place place) {
        return PlaceReview.builder()
                .placeReviewId(placeReviewId)
                .content(content)
                .rating(rating)
                .user(user)
                .place(place)
                .regTime(regTime != null ? regTime : LocalDateTime.now())
                .isHide(isHide != null ? isHide : false)
                .build();
    }
}
