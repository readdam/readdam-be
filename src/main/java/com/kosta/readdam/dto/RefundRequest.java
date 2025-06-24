package com.kosta.readdam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequest {
	private Long orderId;
	private String reason;
}
