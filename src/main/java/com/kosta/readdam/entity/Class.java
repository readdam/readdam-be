package com.kosta.readdam.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "`class`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false, updatable = false)
    private Integer classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_username", referencedColumnName = "username")
    private User leader;

    private String title;
    private String shortIntro;

    private String tag1;
    private String tag2;
    private String tag3;

    private Integer minPerson;
    private Integer maxPerson;

    private String mainImg;
    @Column(columnDefinition = "TEXT")
    private String classIntro;

    private String leaderImg;
    @Column(columnDefinition = "TEXT")
    private String leaderIntro;

    private LocalDateTime round1Date;
    private String round1PlaceName;
    private String round1PlaceLoc;
    private String round1Img;
    @Column(columnDefinition = "TEXT")
    private String round1Content;
    private String round1Bookname;
    private String round1Bookimg;
    private String round1Bookwriter;
    private Double round1Lat;
    private Double round1Log;

    private LocalDateTime round2Date;
    private String round2PlaceName;
    private String round2PlaceLoc;
    private String round2Img;
    @Column(columnDefinition = "TEXT")
    private String round2Content;
    private String round2Bookname;
    private String round2Bookimg;
    private String round2Bookwriter;
    private Double round2Lat;
    private Double round2Log;

    private LocalDateTime round3Date;
    private String round3PlaceName;
    private String round3PlaceLoc;
    private String round3Img;
    @Column(columnDefinition = "TEXT")
    private String round3Content;
    private String round3Bookname;
    private String round3Bookimg;
    private String round3Bookwriter;
    private Double round3Lat;
    private Double round3Log;

    private LocalDateTime round4Date;
    private String round4PlaceName;
    private String round4PlaceLoc;
    private String round4Img;
    @Column(columnDefinition = "TEXT")
    private String round4Content;
    private String round4Bookname;
    private String round4Bookimg;
    private String round4Bookwriter;
    private Double round4Lat;
    private Double round4Log;
}
