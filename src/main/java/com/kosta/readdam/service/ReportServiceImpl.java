package com.kosta.readdam.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kosta.readdam.entity.Report;
import com.kosta.readdam.repository.ReportRepository;
import com.kosta.readdam.repository.spec.ReportSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
	
	private final ReportRepository reportRepository;

    @Override
    public List<Report> getReports(
            String keyword,
            String filterType,
            String category,
            String status,
            String dateType,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Specification<Report> spec = Specification.where(
                ReportSpecification.hasKeyword(filterType, keyword))
            .and(ReportSpecification.hasCategory(category))
            .and(ReportSpecification.hasStatus(status))
            .and(ReportSpecification.betweenDates(dateType, startDate, endDate));

        return reportRepository.findAll(spec);
    }

    @Override
    public Page<Report> getReports(
            String keyword,
            String filterType,
            String category,
            String status,
            String dateType,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Report> spec = Specification.where(
                ReportSpecification.hasKeyword(filterType, keyword))
            .and(ReportSpecification.hasCategory(category))
            .and(ReportSpecification.hasStatus(status))
            .and(ReportSpecification.betweenDates(dateType, startDate, endDate));

        return reportRepository.findAll(spec, pageable);
    }
	

}
