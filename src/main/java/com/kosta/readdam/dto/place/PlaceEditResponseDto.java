package com.kosta.readdam.dto.place;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceEditResponseDto {
    private String name;
    private String location;
    private String phone;
    private String introduce;
    private Double lat;
    private Double log;
    private List<String> tags;
    private List<String> images;
    private List<RoomDto> rooms;
    private List<String> weekdayTimes;
    private List<String> weekendTimes;
}
