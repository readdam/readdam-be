package com.kosta.readdam.dto.place;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import lombok.Builder;

@Getter
@Setter
@Builder
public class HomePlaceSummaryDto {
    private Integer id;
    private String name;
    private String address;
    // private LocalDateTime createDate; // place에 createDate 없어서 pk 내림차순 정렬 예정
    private String type; // "PLACE" or "OTHER_PLACE"
}
