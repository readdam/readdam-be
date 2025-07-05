package com.kosta.readdam.dto.place;

import java.util.List;

import com.kosta.readdam.dto.PlaceReviewDto;
import com.kosta.readdam.util.PageInfo2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaceReviewPageResponse {
	private List<PlaceReviewDto> content;
    private PageInfo2 pageInfo;
}
