package com.kosta.readdam.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.User;

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
public class ClassDto {

    private Integer classId;
    private String leaderUsername;

    private String title;
    private String shortIntro;

    private LocalDateTime createdAt;
    
    private String tag1;
    private String tag2;
    private String tag3;

    private Integer minPerson;
    private Integer maxPerson;

    private String mainImg;
    private String image; // 통합검색 공통 필드 추가
    private String classIntro;

    private String leaderImg;
    private String leaderIntro;
    
    private Boolean isReaddam;	// 장소 '읽담' / '외부' 구분 필드 추가

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate round1Date;
    private String round1PlaceName;
    private String round1PlaceLoc;
    private String round1Img;
    private String round1Content;
    private String round1Bookname;
    private String round1Bookimg;
    private String round1Bookwriter;
    private Double round1Lat;
    private Double round1Log;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate round2Date;
    private String round2PlaceName;
    private String round2PlaceLoc;
    private String round2Img;
    private String round2Content;
    private String round2Bookname;
    private String round2Bookimg;
    private String round2Bookwriter;
    private Double round2Lat;
    private Double round2Log;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate round3Date;
    private String round3PlaceName;
    private String round3PlaceLoc;
    private String round3Img;
    private String round3Content;
    private String round3Bookname;
    private String round3Bookimg;
    private String round3Bookwriter;
    private Double round3Lat;
    private Double round3Log;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate round4Date;
    private String round4PlaceName;
    private String round4PlaceLoc;
    private String round4Img;
    private String round4Content;
    private String round4Bookname;
    private String round4Bookimg;
    private String round4Bookwriter;
    private Double round4Lat;
    private Double round4Log;
    
    private Integer likeCount;
    private Boolean liked;
    private Integer currentParticipants;
    
    // 통합검색 전용 생성자
    public ClassDto(
            Integer classId,
            String title,
            String mainImg,
            String image,
            String tag1,
            String tag2,
            String tag3,
            String shortIntro,
            LocalDate round1Date,
            String round1PlaceName
    ) {
        this.classId = classId;
        this.title = title;
        this.mainImg = mainImg;
        this.image = image;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.shortIntro = shortIntro;
        this.round1Date = round1Date;
        this.round1PlaceName = round1PlaceName;
    }

    public ClassEntity toEntity(User leader) {
        return ClassEntity.builder()
                .classId(classId)
                .leader(leader)
                .title(title)
                .shortIntro(shortIntro)
                .createdAt(createdAt)
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .minPerson(minPerson)
                .maxPerson(maxPerson)
                .mainImg(mainImg)
                .classIntro(classIntro)
                .leaderImg(leaderImg)
                .leaderIntro(leaderIntro)
                .isReaddam(isReaddam)
                .round1Date(round1Date)
                .round1PlaceName(round1PlaceName)
                .round1PlaceLoc(round1PlaceLoc)
                .round1Img(round1Img)
                .round1Content(round1Content)
                .round1Bookname(round1Bookname)
                .round1Bookimg(round1Bookimg)
                .round1Bookwriter(round1Bookwriter)
                .round1Lat(round1Lat)
                .round1Log(round1Log)
                .round2Date(round2Date)
                .round2PlaceName(round2PlaceName)
                .round2PlaceLoc(round2PlaceLoc)
                .round2Img(round2Img)
                .round2Content(round2Content)
                .round2Bookname(round2Bookname)
                .round2Bookimg(round2Bookimg)
                .round2Bookwriter(round2Bookwriter)
                .round2Lat(round2Lat)
                .round2Log(round2Log)
                .round3Date(round3Date)
                .round3PlaceName(round3PlaceName)
                .round3PlaceLoc(round3PlaceLoc)
                .round3Img(round3Img)
                .round3Content(round3Content)
                .round3Bookname(round3Bookname)
                .round3Bookimg(round3Bookimg)
                .round3Bookwriter(round3Bookwriter)
                .round3Lat(round3Lat)
                .round3Log(round3Log)
                .round4Date(round4Date)
                .round4PlaceName(round4PlaceName)
                .round4PlaceLoc(round4PlaceLoc)
                .round4Img(round4Img)
                .round4Content(round4Content)
                .round4Bookname(round4Bookname)
                .round4Bookimg(round4Bookimg)
                .round4Bookwriter(round4Bookwriter)
                .round4Lat(round4Lat)
                .round4Log(round4Log)
                .build();
    }

   
}
