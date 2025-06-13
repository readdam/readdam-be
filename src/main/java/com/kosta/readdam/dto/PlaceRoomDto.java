package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRoomDto {

    private Integer placeRoomId;
    private Integer placeId;
    private Integer point;
    private Integer minPerson;
    private Integer maxPerson;
    private String name;
    private String introduce;
    private String size;

    private Boolean hasAirConditioner;
    private Boolean hasHeater;
    private Boolean hasWifi;
    private Boolean hasPowerOutlet;
    private Boolean hasWhiteboard;
    private Boolean hasWindow;
    private Boolean hasTv;
    private Boolean hasProjector;

    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;
    private String img6;
    private String img7;
    private String img8;
    private String img9;
    private String img10;

    public PlaceRoom toEntity(Place place) {
        return PlaceRoom.builder()
                .placeRoomId(placeRoomId)
                .place(place)
                .point(point)
                .minPerson(minPerson)
                .maxPerson(maxPerson)
                .name(name)
                .introduce(introduce)
                .size(size)
                .hasAirConditioner(hasAirConditioner)
                .hasHeater(hasHeater)
                .hasWifi(hasWifi)
                .hasPowerOutlet(hasPowerOutlet)
                .hasWhiteboard(hasWhiteboard)
                .hasWindow(hasWindow)
                .hasTv(hasTv)
                .hasProjector(hasProjector)
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
}
