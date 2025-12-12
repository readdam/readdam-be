package com.kosta.readdam.dto.place;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import lombok.Builder;

@Getter
@Setter
@Builder
public class PlaceDetailResponseDto {
    private String name;
    private String basicAddress;
    private String detailAddress;
    private String phone;
    private String introduce;
    private Double lat;
    private Double lng;
    private List<String> tags;
    private List<String> images;
    private List<RoomDto> rooms;
    private List<String> weekdayTimes;
    private List<String> weekendTimes;
    private boolean liked;      // 유저가 좋아요 했는지
    private int likeCount;      // 총 좋아요 개수
}

