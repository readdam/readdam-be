package com.kosta.readdam.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ClassCardDto {
	private Integer classId;

    private String title;
    private String shortIntro;

    private String tag1;
    private String tag2;
    private String tag3;

    private Integer minPerson;
    private Integer maxPerson;

    private String mainImg;
    
    private LocalDate round1Date;
    private String round1PlaceName;
    
//    private Integer likeCnt;
//    private Integer currentParticipants;
    
    
	public ClassCardDto(Integer classId, String title, String shortIntro, String tag1,
			String tag2, String tag3, Integer minPerson, Integer maxPerson, String mainImg, LocalDate round1Date,
			String round1PlaceName
//			Integer likeCnt,
//			Integer currentParticipants
			) {
		super();
		this.classId = classId;
		this.title = title;
		this.shortIntro = shortIntro;
		this.tag1 = tag1;
		this.tag2 = tag2;
		this.tag3 = tag3;
		this.minPerson = minPerson;
		this.maxPerson = maxPerson;
		this.mainImg = mainImg;
		this.round1Date = round1Date;
		this.round1PlaceName = round1PlaceName;
//		this.likeCnt = likeCnt;
//		this.currentParticipants = currentParticipants;
	}

    

}
