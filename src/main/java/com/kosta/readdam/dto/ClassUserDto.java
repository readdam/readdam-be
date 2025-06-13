package com.kosta.readdam.dto;

import java.time.LocalDateTime;

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
public class ClassUserDto {

    private Long id;
    private Integer classId;
    private String username;
    private LocalDateTime joinDate;
    private LocalDateTime leftDate;
}
