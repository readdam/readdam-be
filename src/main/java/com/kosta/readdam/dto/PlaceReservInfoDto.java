package com.kosta.readdam.dto;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@NoArgsConstructor
public class PlaceReservInfoDto {
    private Integer reservationId;   // ← 추가
    private String placeName;
    private String placeAddress;
    private String roomName;
    private List<String> dates;
    private double lat;
    private double log;
    
    @QueryProjection
    @Builder
    public PlaceReservInfoDto(
            Integer reservationId,       // ← 추가
            String placeName,
            String placeAddress,
            String roomName,
            List<String> dates,
            double lat,
            double log
    ) {
        super();
        this.reservationId = reservationId;
        this.placeName     = placeName;
        this.placeAddress  = placeAddress;
        this.roomName      = roomName;
        this.dates         = dates;
        this.lat           = lat;
        this.log           = log;
    }
}
