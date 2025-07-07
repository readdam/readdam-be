package com.kosta.readdam.dto.place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceReviewRequest {
    private String content;
    private Integer rating;
    private Boolean isHide;
    private Integer placeId;
}
