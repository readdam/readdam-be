package com.kosta.readdam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kosta.readdam.dto.PlaceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", updatable = false, nullable = false)
    private Integer placeId;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String location;

    @Column(precision = 9, scale = 6)
    private Double lat;

    @Column(precision = 9, scale = 6)
    private Double log;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String introduce;

    @Column(length = 50)
    private String tag1;

    @Column(length = 50)
    private String tag2;

    @Column(length = 50)
    private String tag3;

    @Column(length = 50)
    private String tag4;

    @Column(length = 50)
    private String tag5;

    @Column(length = 50)
    private String tag6;

    @Column(length = 50)
    private String tag7;

    @Column(length = 50)
    private String tag8;

    @Column(length = 50)
    private String tag9;

    @Column(length = 50)
    private String tag10;

    @Column(length = 255)
    private String img1;

    @Column(length = 255)
    private String img2;

    @Column(length = 255)
    private String img3;

    @Column(length = 255)
    private String img4;

    @Column(length = 255)
    private String img5;

    @Column(length = 255)
    private String img6;

    @Column(length = 255)
    private String img7;

    @Column(length = 255)
    private String img8;

    @Column(length = 255)
    private String img9;

    @Column(length = 255)
    private String img10;
    
    public PlaceDto toDto() {
        return PlaceDto.builder()
                .placeId(this.placeId)  // PlaceDto에 id 필드가 있을 것
                .name(this.name)
                .location(this.location)
                .phone(this.phone)
                .introduce(this.introduce)
                .lat(this.lat)
                .log(this.log)
                .tag1(this.tag1)
                .tag2(this.tag2)
                .tag3(this.tag3)
                .tag4(this.tag4)
                .tag5(this.tag5)
                .tag6(this.tag6)
                .tag7(this.tag7)
                .tag8(this.tag8)
                .tag9(this.tag9)
                .tag10(this.tag10)
                .img1(this.img1)
                .img2(this.img2)
                .img3(this.img3)
                .img4(this.img4)
                .img5(this.img5)
                .img6(this.img6)
                .img7(this.img7)
                .img8(this.img8)
                .img9(this.img9)
                .img10(this.img10)
                .build();
    }
    
    public void updateFromDto(PlaceDto dto) {
        this.name = dto.getName();
        this.location = dto.getLocation();
        this.phone = dto.getPhone();
        this.introduce = dto.getIntroduce();
        this.lat = dto.getLat();
        this.log = dto.getLog();
        this.tag1 = dto.getTag1();
        this.tag2 = dto.getTag2();
        this.tag3 = dto.getTag3();
        this.tag4 = dto.getTag4();
        this.tag5 = dto.getTag5();
        this.tag6 = dto.getTag6();
        this.tag7 = dto.getTag7();
        this.tag8 = dto.getTag8();
        this.tag9 = dto.getTag9();
        this.tag10 = dto.getTag10();
        this.img1 = dto.getImg1();
        this.img2 = dto.getImg2();
        this.img3 = dto.getImg3();
        this.img4 = dto.getImg4();
        this.img5 = dto.getImg5();
        this.img6 = dto.getImg6();
        this.img7 = dto.getImg7();
        this.img8 = dto.getImg8();
        this.img9 = dto.getImg9();
        this.img10 = dto.getImg10();
    }

}