package com.kosta.readdam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportRequest {
	private String category;
	private String categoryId;
	private String reason;
	private String content;
	private String reportedUsername;
}
