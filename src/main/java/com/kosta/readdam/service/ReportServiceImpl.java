// src/main/java/com/kosta/readdam/service/ReportServiceImpl.java
package com.kosta.readdam.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.CreateReportRequest;
import com.kosta.readdam.dto.ReportDto;
import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ReportCategory;
import com.kosta.readdam.entity.enums.ReportStatus;
import com.kosta.readdam.repository.BookReviewRepository;
import com.kosta.readdam.repository.ClassQnaRepository;
import com.kosta.readdam.repository.ClassReviewRepository;
import com.kosta.readdam.repository.ReportRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteRepository;
import com.kosta.readdam.repository.WriteShortRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceReviewRepository;
import com.kosta.readdam.repository.place.PlaceReviewRepository;
import com.kosta.readdam.repository.spec.ReportSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final JdbcTemplate jdbc;

	private final ReportRepository reportRepository;
	private final WriteShortRepository writeShortRepo;
	private final WriteCommentRepository commentRepo;
	private final WriteRepository postRepo;
	private final BookReviewRepository bookReviewRepo;
	private final ClassQnaRepository classQnARepo;
	private final ClassReviewRepository classReviewRepo;
	private final PlaceReviewRepository placeReviewRepo;
	private final OtherPlaceReviewRepository otherPlaceReviewRepo;
	private final UserRepository userRepository;

	@Override
	public List<Report> getReports(String keyword, String filterType, String category, String status, String dateType,
			LocalDate startDate, LocalDate endDate) {
		Specification<Report> spec = Specification.where(ReportSpecification.hasKeyword(filterType, keyword))
				.and(ReportSpecification.hasCategory(category)).and(ReportSpecification.hasStatus(status))
				.and(ReportSpecification.betweenDates(dateType, startDate, endDate));

		return reportRepository.findAll(spec);
	}

	@Override
	public Page<Report> getReports(String keyword, String filterType, String category, String status, String dateType,
			LocalDate startDate, LocalDate endDate, Pageable pageable) {
		Specification<Report> spec = Specification.where(ReportSpecification.hasKeyword(filterType, keyword))
				.and(ReportSpecification.hasCategory(category)).and(ReportSpecification.hasStatus(status))
				.and(ReportSpecification.betweenDates(dateType, startDate, endDate));

		return reportRepository.findAll(spec, pageable);
	}

	@Override
	public ReportDto getReportDetail(Integer reportId) {
		Report r = reportRepository.findById(reportId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid report id"));

		ReportDto dto = r.toDto();

		String cat = r.getCategory() != null ? r.getCategory().name() : null;
		String catId = r.getCategoryId();
		
		if (cat == null) {
		    return dto;
		}

		switch (cat) {
		case "write_short":
			writeShortRepo.findById(Integer.valueOf(catId))
					.ifPresent(ws -> dto.setContentPk(ws.getWriteshortId().toString()));
			break;
		case "write":
			postRepo.findById(Integer.valueOf(catId)).ifPresent(w -> dto.setContentPk(w.getWriteId().toString()));
			break;
		case "write_comment":
			commentRepo.findById(Integer.valueOf(catId)).map(WriteCommentDto::from)
					.ifPresent(wcDto -> dto.setContentPk(wcDto.getWriteId().toString()));
			break;
		case "book_review":
			bookReviewRepo.findById(Integer.valueOf(catId))
					.ifPresent(br -> dto.setContentPk(br.getBook().getBookIsbn()));
			break;
		case "class_qna":
			classQnARepo.findById(Integer.valueOf(catId))
					.ifPresent(qna -> dto.setContentPk(qna.getClassEntity().getClassId().toString()));
			break;
		case "class_review":
			classReviewRepo.findById(Integer.valueOf(catId))
					.ifPresent(cr -> dto.setContentPk(cr.getClassEntity().getClassId().toString()));
			break;
		case "place_review":
			placeReviewRepo.findById(Integer.valueOf(catId))
					.ifPresent(pr -> dto.setContentPk(pr.getPlace().getPlaceId().toString()));
			break;
		case "other_place_review":
			otherPlaceReviewRepo.findById(Integer.valueOf(catId))
					.ifPresent(opr -> dto.setContentPk(opr.getOtherPlace().getOtherPlaceId().toString()));
			break;
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
		ReportDto dto = r.toDto();
		dto.setContentPk(null);
		return dto;
	}

	@Transactional
	@Override
	public ReportDto hideContentAndResolve(Integer reportId) {
		// 1) 상태 변경 (RESOLVED)
		ReportDto dto = processReport(reportId, ReportStatus.RESOLVED.name());
		// 2) is_hide = 1
		updateHideFlag(reportId, 1);
		return dto;
	}

	@Transactional
	@Override
	public ReportDto rejectAndUnhide(Integer reportId) {
		// 1) 상태 변경 (REJECTED)
		ReportDto dto = processReport(reportId, ReportStatus.REJECTED.name());
		// 2) is_hide = 0
		updateHideFlag(reportId, 0);
		return dto;
	}

	/** 공통: 테이블별 is_hide 플래그 업데이트 */
	private void updateHideFlag(Integer reportId, int hideFlag) {
		Report r = reportRepository.getById(reportId);
		String table = r.getCategory().name();
	    String pkColumn = r.getCategory().getIdColumn();
		String pk = r.getCategoryId();

		String sql = String.format("UPDATE `%s` SET is_hide = ? WHERE %s = ?", table, pkColumn);
		jdbc.update(sql, hideFlag, Integer.valueOf(pk));
	}
	
	@Transactional
	@Override
	public void saveReport(String reporterUsername, CreateReportRequest req) {

	    User reporter = userRepository.findByUsername(reporterUsername)
	        .orElseThrow(() -> new UsernameNotFoundException("신고자 없음: " + reporterUsername));


	    User reported = userRepository.findById(req.getReportedUsername())
	        .orElseThrow(() -> new IllegalArgumentException("신고 대상 없음: " + req.getReportedUsername()));


	    Report r = Report.builder()
	        .reporter(reporter)
	        .reported(reported)
	        .reason(req.getReason())
	        .content(req.getContent())
	        .category(req.getCategory())
	        .categoryId(req.getCategoryId())
	        .reportedAt(LocalDateTime.now())
	        .status(ReportStatus.PENDING)
	        .build();

	    reportRepository.save(r);
	}
	
	
	@Transactional
	@Override
	public void bulkHideAndResolve(ReportCategory category, String categoryId) {
	    
	    reportRepository.updateStatusByContent(
	        category, categoryId, ReportStatus.RESOLVED, LocalDateTime.now()
	    );

	    String sql = String.format("UPDATE `%s` SET is_hide = 1 WHERE %s = ?",
	                               category.name(), category.getIdColumn());
	    jdbc.update(sql, Integer.valueOf(categoryId));
	}
	
	@Transactional
	@Override
    public void bulkRejectAndUnhide(ReportCategory category, String categoryId) {
        // 상태 일괄 REJECTED
        reportRepository.updateStatusByContent(
            category, categoryId, ReportStatus.REJECTED, LocalDateTime.now()
        );
        // 본문 unhide
        String sql = String.format(
                "UPDATE `%s` SET is_hide = 0 WHERE %s = ?",
                category.name(), category.getIdColumn());

            jdbc.update(sql, Integer.valueOf(categoryId));
        }

}
