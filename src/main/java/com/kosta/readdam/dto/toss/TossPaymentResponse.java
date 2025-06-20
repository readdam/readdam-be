package com.kosta.readdam.dto.toss;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TossPaymentResponse {
	
	private String paymentKey;
	private String nextRedirectUrl;
}
