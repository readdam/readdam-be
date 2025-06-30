package com.kosta.readdam.entity;

import java.time.LocalTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kosta.readdam.dto.OtherPlaceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "other_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "other_place_id", nullable = false, updatable = false)
    private Integer otherPlaceId;

    private String name;

    @Column(length = 255)
    private String basicAddress;

    @Column(length = 255)
    private String detailAddress;

    private Double lat;

    @Column(name = "log") // 실제론 lng
    private Double lng;

    private String phone;

    private String domain;

    @Column(name = "weekday_stime")
    private LocalTime weekdayStime;

    @Column(name = "weekday_etime")
    private LocalTime weekdayEtime;

    @Column(name = "weekend_stime")
    private LocalTime weekendStime;

    @Column(name = "weekend_etime")
    private LocalTime weekendEtime;

    @Column(columnDefinition = "TEXT")
    private String introduce;

    @Column(length = 255)
    private String fee;
    
    @Column(columnDefinition = "TEXT")
    private String usageGuide;

    @Column(columnDefinition = "TEXT")
    private String facilities;

    // ✅ 추가 이미지
    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;

    // ✅ 태그 (키워드)
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;

    // ✅ 주의사항
    @Column(length = 255)
    private String caution;

    public OtherPlaceDto toDto() {
        return OtherPlaceDto.builder()
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
                // ✅ tags 리스트 꼭 넣기
                .tags(
                    Stream.of(tag1, tag2, tag3, tag4, tag5)
                        .filter(Objects::nonNull)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toList())
                )
                // ✅ images 리스트도 넣기
                .images(
                    Stream.of(img1, img2, img3, img4, img5)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                )
                .caution(caution)
                .build();
    }
}
