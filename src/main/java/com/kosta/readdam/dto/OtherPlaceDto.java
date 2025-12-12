package com.kosta.readdam.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;

import com.kosta.readdam.entity.OtherPlace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String usageGuide;
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

    private List<String> tags;
    private List<String> images;
   
    private Long likeCount;
    
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
                .usageGuide(usageGuide)
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
    
    public static OtherPlaceDto fromEntity(OtherPlace place) {
        OtherPlaceDto dto = new OtherPlaceDto();
        BeanUtils.copyProperties(place, dto);

        dto.setTags(
            Stream.of(
                place.getTag1(),
                place.getTag2(),
                place.getTag3(),
                place.getTag4(),
                place.getTag5()
            )
            .filter(Objects::nonNull)
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList())
        );

        dto.setImages(
            Stream.of(
                place.getImg1(),
                place.getImg2(),
                place.getImg3(),
                place.getImg4(),
                place.getImg5()
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
        );

        return dto;
    }

}
