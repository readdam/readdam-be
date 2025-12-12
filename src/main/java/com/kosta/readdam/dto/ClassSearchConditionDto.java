package com.kosta.readdam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassSearchConditionDto {
	private String keyword;
	private String tag;
	private String place;
	private String sort;	// "Latest", "likes", "deadLine"

}
