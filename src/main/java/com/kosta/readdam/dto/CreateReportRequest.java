package com.kosta.readdam.dto;

import com.kosta.readdam.entity.enums.ReportCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportRequest {
	private ReportCategory  category;
	private String categoryId;
	private String reason;
	private String content;
	private String reportedUsername;
}
