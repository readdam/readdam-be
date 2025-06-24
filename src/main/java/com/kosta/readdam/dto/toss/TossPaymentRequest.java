package com.kosta.readdam.dto.toss;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TossPaymentRequest {
	
	private String orderId;
    private String orderName;
    private Integer amount;
    private String successUrl;
    private String failUrl;
	

}
