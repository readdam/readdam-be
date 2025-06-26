package com.kosta.readdam.dto;

import com.kosta.readdam.entity.OtherPlace;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlaceDto {

    private Integer otherPlaceId;
    private String name;
    private String basicAddress;
    private String detailAddress;
    private Double lat;
    private Double lng;
    private String phone;
    private String domain;
    private LocalTime weekdayStime;
    private LocalTime weekdayEtime;
    private LocalTime weekendStime;
    private LocalTime weekendEtime;
    private String introduce;
    private String img;
    private Integer fee;
    private String facilities;

    public OtherPlace toEntity() {
        return OtherPlace.builder()
                .otherPlaceId(otherPlaceId)
                .name(name)
                .basicAddress(basicAddress)
                .detailAddress(detailAddress)
                .lat(lat)
                .lng(lng)
                .phone(phone)
                .domain(domain)
                .weekdayStime(weekdayStime)
                .weekdayEtime(weekdayEtime)
                .weekendStime(weekendStime)
                .weekendEtime(weekendEtime)
                .introduce(introduce)
                .img(img)
                .fee(fee)
                .facilities(facilities)
                .build();
    }
}
