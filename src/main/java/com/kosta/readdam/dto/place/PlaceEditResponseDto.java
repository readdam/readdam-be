package com.kosta.readdam.dto.place;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PlaceEditResponseDto {
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
}
