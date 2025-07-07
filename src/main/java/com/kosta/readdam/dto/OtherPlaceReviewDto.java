package com.kosta.readdam.dto;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceReview;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlaceReviewDto {

    private Integer otherPlaceReviewId;
    private String content;
    private Integer rating;
    private String username;
    private String nickname;
    private LocalDateTime regTime;
    private Integer otherPlaceId;
    private Boolean isHide;

    public OtherPlaceReview toEntity(User user, OtherPlace otherPlace) {
        return OtherPlaceReview.builder()
                .otherPlaceReviewId(otherPlaceReviewId)
                .content(content)
                .rating(rating)
                .user(user)
                .regTime(regTime != null ? regTime : LocalDateTime.now())
                .otherPlace(otherPlace)
                .isHide(isHide != null ? isHide : false)
                .build();
    }
}
