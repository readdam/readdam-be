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
    private Integer fee;
    private String facilities;

    // ✅ 추가 이미지
    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;

    // ✅ 키워드
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;

    // ✅ 주의사항
    private String caution;

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
                .fee(fee)
                .facilities(facilities)
                .img1(img1)
                .img2(img2)
                .img3(img3)
                .img4(img4)
                .img5(img5)
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .tag4(tag4)
                .tag5(tag5)
                .caution(caution)
                .build();
    }
}
