package com.kosta.readdam.dto;

import lombok.Data;

//맞춤법 검사 요청하기 위한 DTO

@Data
public class SpellCheckRequest {
    private String text; // 검사를 수행할 텍스트

}
