package com.kosta.readdam.dto.place;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceReviewUpdateRequest {
    private String content;
    private Integer rating;
    private Boolean isHide;
}