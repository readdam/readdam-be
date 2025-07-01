package com.kosta.readdam.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.CreateReportRequest;
import com.kosta.readdam.dto.ReportDto;
import com.kosta.readdam.entity.Report;

public interface ReportService {

	List<Report> getReports(String keyword, String filterType, String category, String status, String dateType,
			LocalDate startDate, LocalDate endDate);

	Page<Report> getReports(String keyword, String filterType, String category, String status, String dateType,
			LocalDate startDate, LocalDate endDate, Pageable pageable);
	
	ReportDto getReportDetail(Integer reportId);

	ReportDto processReport(Integer reportId, String newStatus);


	ReportDto hideContentAndResolve(Integer reportId);

	ReportDto rejectAndUnhide(Integer reportId);

	void saveReport(String reporterUsername, CreateReportRequest req);

	void bulkHideAndResolve(String category, String categoryId);

	void bulkRejectAndUnhide(String category, String categoryId);
}
