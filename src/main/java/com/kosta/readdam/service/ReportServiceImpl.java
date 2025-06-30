// src/main/java/com/kosta/readdam/service/ReportServiceImpl.java
package com.kosta.readdam.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.ReportDto;
import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.enums.ReportStatus;
import com.kosta.readdam.repository.BookReviewRepository;
import com.kosta.readdam.repository.ReportRepository;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteRepository;
import com.kosta.readdam.repository.spec.ReportSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final JdbcTemplate jdbc;                   // ← 추가

    // 카테고리별 실제 PK 조회용 레포지토리
    private final WriteCommentRepository commentRepo;       // 댓글 → 게시글 ID
    private final WriteRepository postRepo;             // 게시글
    private final BookReviewRepository bookReviewRepo; // 책 후기
    // 필요한 만큼 다른 레포지토리도 추가…

    @Override
    public List<Report> getReports(String keyword, String filterType, String category,
                                   String status, String dateType,
                                   LocalDate startDate, LocalDate endDate) {
        Specification<Report> spec = Specification.where(
                ReportSpecification.hasKeyword(filterType, keyword))
            .and(ReportSpecification.hasCategory(category))
            .and(ReportSpecification.hasStatus(status))
            .and(ReportSpecification.betweenDates(dateType, startDate, endDate));

        return reportRepository.findAll(spec);
    }

    @Override
    public Page<Report> getReports(String keyword, String filterType, String category,
                                   String status, String dateType,
                                   LocalDate startDate, LocalDate endDate,
                                   Pageable pageable) {
        Specification<Report> spec = Specification.where(
                ReportSpecification.hasKeyword(filterType, keyword))
            .and(ReportSpecification.hasCategory(category))
            .and(ReportSpecification.hasStatus(status))
            .and(ReportSpecification.betweenDates(dateType, startDate, endDate));

        return reportRepository.findAll(spec, pageable);
    }

    @Override
    public ReportDto getReportDetail(Integer reportId) {
        Report r = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid report id"));

        ReportDto dto = r.toDto();

        String cat       = r.getCategory();
        String catId     = r.getCategoryId();
        String contentPk = null;

        switch (cat) {
            case "write_comment":
                commentRepo.findById(Integer.valueOf(catId))
                    .map(WriteCommentDto::from)               // 엔티티 → DTO
                    .ifPresent(wcDto ->                        // DTO에서 writeId 꺼내기
                        dto.setContentPk(wcDto.getWriteId().toString())
                    );
                break;

            case "write":
                postRepo.findById(Integer.valueOf(catId))
                    .ifPresent(write ->
                        dto.setContentPk(write.getWriteId().toString())
                    );
                break;

            case "book_review":
                bookReviewRepo.findById(Integer.valueOf(catId))
                    .ifPresent(br ->
                        dto.setContentPk(br.getBook().getBookIsbn())
                    );
                break;

            // …다른 카테고리도 동일 패턴으로 추가
        }

        return dto;
    }





    @Override
    public ReportDto processReport(Integer reportId, String newStatus) {
        Report r = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid report id"));
        ReportStatus st = ReportStatus.valueOf(newStatus);
        r.setStatus(st);
        r.setProcessedAt(LocalDateTime.now());
        reportRepository.save(r);
        // contentPk는 상세 조회와 동일 로직 필요 없으면 null
        ReportDto dto = r.toDto();
        dto.setContentPk(null);
        return dto;
    }


    @Override
    public ReportDto hideContentAndResolve(Integer reportId) {
        // 먼저 상태 변경
        ReportDto dto = processReport(reportId, ReportStatus.RESOLVED.name());
        // 그 다음 실제 숨김 처리
        Report r = reportRepository.getById(reportId);
        String table = r.getCategory();
        String pk    = r.getCategoryId();
        jdbc.update("UPDATE " + table + " SET is_hide = 1 WHERE id = ?", Integer.valueOf(pk));
        return dto;
    }
}
