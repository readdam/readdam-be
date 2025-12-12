package com.kosta.readdam.dto.place;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceSummaryDto {
    private Integer placeId;
    private String name;
    private String basicAddress;
    private String detailAddress;   
    private String introduce;
    private String phone;
    private Long roomCount;
    private List<String> tags;      // ← 태그 최대 10개
    private String thumbnailImage;  // ← 대표 이미지 (가장 먼저 등록된 사진)
    private List<String> images;
    private List<String> weekdayTime;
    private List<String> weekendTime;
    private Integer likeCount;
}