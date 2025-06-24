package com.kosta.readdam.dto.toss;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TossPaymentConfirmResponse {

	private String paymentKey;
    private String orderId;
    private int amount;
    private String currency;
    private String status;
}
