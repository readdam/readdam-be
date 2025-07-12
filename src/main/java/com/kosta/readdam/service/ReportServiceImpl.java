// src/main/java/com/kosta/readdam/service/ReportServiceImpl.java
package com.kosta.readdam.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteComment;
import com.kosta.readdam.entity.enums.ReportCategory;
import com.kosta.readdam.entity.enums.ReportStatus;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.BookReviewRepository;
import com.kosta.readdam.repository.ReportRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteRepository;
import com.kosta.readdam.repository.WriteShortRepository;
import com.kosta.readdam.repository.klass.ClassQnaRepository;
import com.kosta.readdam.repository.klass.ClassReviewRepository;
import com.kosta.readdam.repository.otherPlace.OtherPlaceReviewRepository;
import com.kosta.readdam.repository.place.PlaceReviewRepository;
import com.kosta.readdam.repository.spec.ReportSpecification;
import com.kosta.readdam.service.alert.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	private final AlertRepository alertRepository;
	private final NotificationService notificationService;
	
	 private static final Map<ReportCategory, String> CATEGORY_PATHS = Map.of(
		        ReportCategory.write,         "myWrite",
		        ReportCategory.write_short,   "myWriteShort",
		        ReportCategory.write_comment, "myWriteComment",
		        ReportCategory.book_review,   "myReviewBook",
		        ReportCategory.class_review,  "myReviewClass"
		    );

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

		Report report = reportRepository.findById(reportId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid report id"));

		if (report.getCategory() == ReportCategory.write_comment) {
			Integer commentId = Integer.valueOf(report.getCategoryId());
			WriteComment comment = commentRepo.findById(commentId)
					.orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

			if (!Boolean.TRUE.equals(comment.getIsHide())) {
				comment.setIsHide(true);
				commentRepo.save(comment);

				// 댓글 수 -1 처리
				Integer writeId = comment.getWrite().getWriteId();
				postRepo.updateCommentCnt(writeId, -1);
			}

			return dto;

		} else {
			// 글, 기타 신고는 그대로 updateHideFlag로 처리
			User reported = report.getReported();

			String type = "report";
			String title = String.format("신고 #%d 처리 완료", reportId);
			String body = "귀하의 게시물이 신고 처리되어 숨김 처리되었습니다.";

			// 중복 체크: 같은 수신자·type·title 이 없을 때만
			if (!alertRepository.existsByReceiverUsernameAndTypeAndTitle(reported.getUsername(), type, title)) {

				// 시스템 발신자 조회
				User system = userRepository.findByUsername("system")
						.orElseThrow(() -> new IllegalStateException("system 계정이 없습니다."));

				// Alert 생성·저장
				Alert alert = Alert.builder().sender(system).receiver(reported).type(type).title(title).content(body)
						.build();
				alertRepository.save(alert);

				// FCM 푸시
				Map<String, String> data = Map.of("type", type);
				notificationService.sendPush(reported.getUsername(), title, body, data);
			}

			return dto;
		}
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
		String table = r.getCategory().getTableName();
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

		Report r = Report.builder().reporter(reporter).reported(reported).reason(req.getReason())
				.content(req.getContent()).category(req.getCategory()).categoryId(req.getCategoryId())
				.reportedAt(LocalDateTime.now()).status(ReportStatus.PENDING).build();

		reportRepository.save(r);
	}

	  @Transactional
	    @Override
	    public void bulkHideAndResolve(ReportCategory category, String categoryId) {
	        // 1) 상태 일괄 RESOLVED
	        reportRepository.updateStatusByContent(category, categoryId, ReportStatus.RESOLVED, LocalDateTime.now());

	        // 2) 숨김 처리
	        if (category == ReportCategory.write_comment) {
	            Integer commentId = Integer.valueOf(categoryId);
	            WriteComment comment = commentRepo.findById(commentId)
	                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
	            if (!Boolean.TRUE.equals(comment.getIsHide())) {
	                comment.setIsHide(true);
	                commentRepo.save(comment);
	                postRepo.updateCommentCnt(comment.getWrite().getWriteId(), -1);
	            }
	        } else {
	            String sql = String.format("UPDATE `%s` SET is_hide = 1 WHERE %s = ?", 
	                category.getTableName(), category.getIdColumn());
	            jdbc.update(sql, Integer.valueOf(categoryId));
	        }

	        // 3) 알림용 Report 조회
	        List<Report> list = reportRepository.findByCategoryAndCategoryId(category, categoryId);
	        if (list.isEmpty()) return;
	        User reported = list.get(0).getReported();

	        String type  = "report";
	        String title = String.format("신고 #%s 처리 완료", categoryId);
	        String body  = "귀하의 게시물이 신고 처리되어 숨김 처리되었습니다.";

	        // 4) 중복 체크 후 Alert 저장 & FCM 푸시
	        if (!alertRepository.existsByReceiverUsernameAndTypeAndTitle(reported.getUsername(), type, title)) {
	            User system = userRepository.findByUsername("system")
	                .orElseThrow(() -> new IllegalStateException("system 계정이 없습니다."));

	            Alert.AlertBuilder builder = Alert.builder()
	                .sender(system)
	                .receiver(reported)
	                .type(type)
	                .title(title)
	                .content(body);

	            String path = CATEGORY_PATHS.get(category);
	            if (path != null) {
	                builder.linkUrl(path);
	            }

	            Alert alert = builder.build();
	            alertRepository.save(alert);

	            Map<String, String> data = new HashMap<>();
	            data.put("type", type);
	            if (path != null) {
	                data.put("linkUrl", path);
	            }
	            notificationService.sendPush(reported.getUsername(), title, body, data);
	        }
	    }

	    @Transactional
	    @Override
	    public void bulkRejectAndUnhide(ReportCategory category, String categoryId) {
	        // 1) 상태 일괄 REJECTED
	        log.info("bulkRejectAndUnhide 호출 → category={} / categoryId={}", category, categoryId);
	        reportRepository.updateStatusByContent(category, categoryId, ReportStatus.REJECTED, LocalDateTime.now());

	        // 2) 본문 unhide
	        String sql = String.format("UPDATE `%s` SET is_hide = 0 WHERE %s = ?", 
	            category.getTableName(), category.getIdColumn());
	        jdbc.update(sql, Integer.valueOf(categoryId));

	        // 3) 알림용 Report 조회
	        Report any = reportRepository.findFirstByCategoryAndCategoryId(category, categoryId).orElseThrow();
	        User reported = any.getReported();

	        String type  = "report";
	        String title = String.format("신고 #%s 반려 및 복구 완료", categoryId);
	        String body  = "귀하의 게시물이 신고가 반려되어 다시 보이게 되었습니다.";

	        // 4) 중복 체크 후 Alert 저장 & FCM 푸시
	        if (!alertRepository.existsByReceiverUsernameAndTypeAndTitle(reported.getUsername(), type, title)) {
	            User system = userRepository.findByUsername("system").orElseThrow();

	            Alert.AlertBuilder builder = Alert.builder()
	                .sender(system)
	                .receiver(reported)
	                .type(type)
	                .title(title)
	                .content(body);

	            String path = CATEGORY_PATHS.get(category);
	            if (path != null) {
	                builder.linkUrl(path);
	            }

	            Alert alert = builder.build();
	            alertRepository.save(alert);

	            Map<String, String> data = new HashMap<>();
	            data.put("type", type);
	            if (path != null) {
	                data.put("linkUrl", path);
	            }
	            notificationService.sendPush(reported.getUsername(), title, body, data);
	        }
	    }
}
