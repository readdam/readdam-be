package com.kosta.readdam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmTokenRequest {

    /** 클라이언트가 새로 발급받은 FCM 토큰 */
    private String token;
}