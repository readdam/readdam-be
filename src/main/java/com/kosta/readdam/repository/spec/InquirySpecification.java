package com.kosta.readdam.repository.spec;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.InquiryStatus;

public class InquirySpecification {
    public static Specification<Inquiry> hasKeyword(String filterType, String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            switch (filterType) {
                case "title":
                    return cb.like(root.get("title"), "%" + keyword + "%");
                case "content":
                    return cb.like(root.get("content"), "%" + keyword + "%");
                case "author":
                    Join<Inquiry, User> userJoin = root.join("user", JoinType.INNER);
                    return cb.like(userJoin.get("username"), "%" + keyword + "%");
                default:
                    return null;
            }
        };
    }

    public static Specification<Inquiry> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) {
                return null;
            }
            InquiryStatus st;
            try {
                st = InquiryStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return null;
            }
            return cb.equal(root.get("status"), st);
        };
    }

    public static Specification<Inquiry> betweenDates(
            String dateType, LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            Path<LocalDateTime> path = "답변일".equals(dateType)
                    ? root.get("answerDate")
                    : root.get("regDate");

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
