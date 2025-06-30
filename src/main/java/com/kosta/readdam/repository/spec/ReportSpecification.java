package com.kosta.readdam.repository.spec;


import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ReportStatus;

public class ReportSpecification {

    public static Specification<Report> hasKeyword(String filterType, String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            if ("reporter".equals(filterType)) {
                Join<Report, User> reporter = root.join("reporter", JoinType.INNER);
                return cb.like(reporter.get("username"), "%" + keyword + "%");
            } else {
                return cb.like(root.get("reason"), "%" + keyword + "%");
            }
        };
    }

    public static Specification<Report> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<Report> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return null;
            ReportStatus st;
            try {
                st = ReportStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return null;  // 잘못된 값이면 조건 무시
            }
            return cb.equal(root.get("status"), st);
        };
    }

    public static Specification<Report> betweenDates(
            String dateType, LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            Path<LocalDateTime> path = 
                "처리일".equals(dateType)
                    ? root.get("processedAt")
                    : root.get("reportedAt");

            Predicate p1 = (start != null)
                ? cb.greaterThanOrEqualTo(path, start.atStartOfDay())
                : null;
            Predicate p2 = (end != null)
                ? cb.lessThanOrEqualTo(path, end.atTime(23, 59, 59))
                : null;

            if (p1 != null && p2 != null) return cb.and(p1, p2);
            if (p1 != null) return p1;
            if (p2 != null) return p2;
            return null;
        };
    }
}