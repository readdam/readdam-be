package com.kosta.readdam.controller.admin;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.ReportDto;
import com.kosta.readdam.entity.Report;
import com.kosta.readdam.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {

	private final ReportService service;

	public ReportController(ReportService service) {
		this.service = service;
	}

	@GetMapping
	public List<ReportDto> listReports(@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "reporter") String filterType, @RequestParam(required = false) String category,
			@RequestParam(required = false) String status, // 이건 한글로 옴
			@RequestParam(defaultValue = "접수일") String dateType,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		// 프론트의 한글 상태를 영어 enum 이름으로 변환
		String enumStatus = null;
		if (status != null && !status.isBlank()) {
			switch (status) {
			case "미처리":
				enumStatus = "PENDING";
				break;
			case "처리":
				enumStatus = "RESOLVED";
				break;
			case "반려":
				enumStatus = "REJECTED";
				break;
			default:
				enumStatus = status; // 혹시 영어 코드일 때
			}
		}
		List<Report> list = service.getReports(keyword, filterType, category, enumStatus, dateType, startDate, endDate);
		return list.stream().map(Report::toDto).collect(Collectors.toList());
	}
}
