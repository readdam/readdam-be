package com.kosta.readdam.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassCardDto {
	private Integer classId;
    private String leaderUsername;

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
    private String round1PlaceLoc;
    
    private Integer likeCount;
    private Boolean liked;
    private Integer currentParticipants;
   

}
