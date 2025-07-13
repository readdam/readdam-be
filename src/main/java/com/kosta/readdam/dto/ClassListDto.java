package com.kosta.readdam.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ClassListDto {
    private Integer no;
    private Integer classId;
    private String title;
    private String leaderName;
    private String leaderNickname;
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // 모집중 / 진행중 / 종료
}
