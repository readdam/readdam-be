package com.kosta.readdam.entity;

import javax.persistence.*;

import lombok.*;

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
}
