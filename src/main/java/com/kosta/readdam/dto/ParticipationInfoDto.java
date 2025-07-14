package com.kosta.readdam.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ParticipationInfoDto {

	private int usedPoints;
    private LocalDate cancelableUntil;
    private boolean joined;             // 이미 참여했는지
    private boolean canCancel;          // 취소 가능 여부
    private int currentParticipants;    // 현재 참여 인원
}
