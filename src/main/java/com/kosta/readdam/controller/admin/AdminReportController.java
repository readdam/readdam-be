// src/main/java/com/kosta/readdam/controller/admin/ReportController.java
package com.kosta.readdam.controller.admin;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.dto.ReportDto;
import com.kosta.readdam.entity.Report;
import com.kosta.readdam.service.ReportService;
import com.kosta.readdam.util.PageInfo2;

@RestController
@RequestMapping("/admin/report")
public class AdminReportController {

    private final ReportService service;

    public AdminReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping
    public PagedResponse<ReportDto> listReports(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "user") String filterType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,    // 한글 상태
            @RequestParam(defaultValue = "접수일") String dateType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 한글 상태 → 영문 enum 코드 매핑
        String enumStatus = null;
        if (status != null && !status.isBlank()) {
            switch (status) {
                case "미처리": enumStatus = "PENDING"; break;
                case "처리":   enumStatus = "RESOLVED"; break;
                case "반려":   enumStatus = "REJECTED"; break;
                default:       enumStatus = status;
            }
        }
        // Pageable 생성 (0-based page index)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("reportedAt").descending());

        // 페이징된 Report 조회
        Page<Report> paged = service.getReports(
            keyword, filterType, category, enumStatus,
            dateType, startDate, endDate,
            pageable
        );

        // 엔티티 → DTO 변환
        List<ReportDto> dtoList = paged.stream()
            .map(Report::toDto)
            .collect(Collectors.toList());

        // PageInfo2 생성
        PageInfo2 pageInfo = PageInfo2.from(paged);

        // PagedResponse 반환
        return new PagedResponse<>(dtoList, pageInfo);
    }
    
    @GetMapping("/{id}")
    public ReportDto detail(@PathVariable("id") Integer id) {
        return service.getReportDetail(id);
    }

    /**
     * 신고 반려 처리 (REJECTED + 해당 콘텐츠 is_hide = 0)
     */
    @PutMapping("/{id}/reject")
    public ReportDto reject(@PathVariable("id") Integer id) {
        return service.rejectAndUnhide(id);
    }

    /**
     * 신고 숨김 처리 (RESOLVED + 해당 콘텐츠 is_hide = 1)
     */
    @PutMapping("/{id}/hide")
    public ReportDto hide(@PathVariable("id") Integer id) {
        return service.hideContentAndResolve(id);
    }
    
    @PostMapping("/bulk-hide")
    public ResponseEntity<Void> bulkHide(
        @RequestParam String category,
        @RequestParam String categoryId
    ) {
    	service.bulkHideAndResolve(category, categoryId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/bulk-reject")
    public ResponseEntity<Void> bulkReject(
        @RequestParam String category,
        @RequestParam String categoryId
    ) {
    	service.bulkRejectAndUnhide(category, categoryId);
        return ResponseEntity.ok().build();
    }

}
