package com.kosta.readdam.entity;

import com.kosta.readdam.dto.OtherPlaceDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

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

    @Column(name = "log") // DB에선 log지만 실무에선 lng로 생각됨
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

    private String img;

    private Integer fee;

    @Column(columnDefinition = "TEXT")
    private String facilities;

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
                .img(img)
                .fee(fee)
                .facilities(facilities)
                .build();
    }
}
