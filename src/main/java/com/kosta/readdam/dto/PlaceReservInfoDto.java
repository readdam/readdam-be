package com.kosta.readdam.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceReservInfoDto {
    private String placeName;
    private String placeAddress;
    private String roomName;
    private List<String> dates;
    private double lat;
    private double log;
    
}
