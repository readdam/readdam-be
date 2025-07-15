package com.kosta.readdam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kosta.readdam.dto.PlaceRoomDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "place_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_room_id", updatable = false, nullable = false)
    private Integer placeRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column
    private Integer point;

    @Column(name = "min_person")
    private Integer minPerson;

    @Column(name = "max_person")
    private Integer maxPerson;

    @Column(length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String introduce;

    @Column(length = 100)
    private String size;

    @Column(name = "has_air_conditioner", nullable = false)
    private Boolean hasAirConditioner;

    @Column(name = "has_heater", nullable = false)
    private Boolean hasHeater;

    @Column(name = "has_wifi", nullable = false)
    private Boolean hasWifi;

    @Column(name = "has_power_outlet", nullable = false)
    private Boolean hasPowerOutlet;

    @Column(name = "has_whiteboard", nullable = false)
    private Boolean hasWhiteboard;

    @Column(name = "has_window", nullable = false)
    private Boolean hasWindow;

    @Column(name = "has_tv", nullable = false)
    private Boolean hasTv;

    @Column(name = "has_projector", nullable = false)
    private Boolean hasProjector;

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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")          
    private ClassEntity classEntity;
    
    public PlaceRoom toEntity(Place place) {
        return PlaceRoom.builder()
                .place(place)
                .name(name)
                .introduce(introduce)
                .size(size)
                .minPerson(minPerson)
                .maxPerson(maxPerson)
                .hasAirConditioner(hasAirConditioner)
                .hasHeater(hasHeater)
                .hasWifi(hasWifi)
                .hasWindow(hasWindow)
                .hasPowerOutlet(hasPowerOutlet)
                .hasTv(hasTv)
                .hasProjector(hasProjector)
                .hasWhiteboard(hasWhiteboard)
                .img1(img1)
                .img2(img2)
                .img3(img3)
                .img4(img4)
                .img5(img5)
                .img6(img6)
                .img7(img7)
                .img8(img8)
                .img9(img9)
                .img10(img10)
                .build();
    }

    public void updateFromDto(PlaceRoomDto dto) {
        this.name = dto.getName();
        this.introduce = dto.getIntroduce();
        this.size = dto.getSize();
        this.minPerson = dto.getMinPerson();
        this.maxPerson = dto.getMaxPerson();

        this.hasAirConditioner = dto.getHasAirConditioner();
        this.hasHeater = dto.getHasHeater();
        this.hasWifi = dto.getHasWifi();
        this.hasWindow = dto.getHasWindow();
        this.hasPowerOutlet = dto.getHasPowerOutlet();
        this.hasTv = dto.getHasTv();
        this.hasProjector = dto.getHasProjector();
        this.hasWhiteboard = dto.getHasWhiteboard();

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
