package com.kosta.readdam.dto.otherPlace;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlaceSummaryDto {
	private Integer otherPlaceId;
    private String name;
    private String basicAddress;
    private String detailAddress;
    private String phone;
    private String domain;
    private String introduce;
    private String img1;
    private LocalTime weekdayStime;
    private LocalTime weekdayEtime;
    private LocalTime weekendStime;
    private LocalTime weekendEtime;
    private Long likeCount;
    private List<String> tags;
    
    // 만약 tags를 수동으로 채울거면 tag1~tag5도 넣어둬야 합니다
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
}
