package com.kosta.readdam.dto.otherPlace;

import java.util.List;

import com.kosta.readdam.dto.OtherPlaceReviewDto;
import com.kosta.readdam.util.PageInfo2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtherPlaceReviewPageResponse {
    private List<OtherPlaceReviewDto> content;
    private PageInfo2 pageInfo;
}