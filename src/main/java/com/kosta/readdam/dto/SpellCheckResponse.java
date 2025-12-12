package com.kosta.readdam.dto;

import java.util.List;

import lombok.Data;

// 맞춤법 검사 결과를 전달하기 위한 DTO
@Data
public class SpellCheckResponse {

	// 교정 제안 목록
	// 한 문장에 여러 개의 오타/띄어쓰기 문제가 있을 수 있으므로 List로 선언
	private List<Correction> corrections;
    private String errorMessage;

	@Data
	public static class Correction {
	    private String orgStr; //원래 단어 
	    private String candWord; // 교정된 단어 제안 
	    private String errorType; // 에러 유형 (e.g. 오타, 띄어쓰기 ..)
	    private Integer start; // 시작 인덱스
	    private Integer end; // 끝 인덱스
	    private String help; // 도움말
	}
	
}