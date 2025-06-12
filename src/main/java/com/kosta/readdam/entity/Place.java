package com.kosta.readdam.entity;

import javax.persistence.*;

import lombok.*;

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
}
