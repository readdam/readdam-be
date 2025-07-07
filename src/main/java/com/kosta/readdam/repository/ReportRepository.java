package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.enums.ReportCategory;
import com.kosta.readdam.entity.enums.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
	
	@Modifying
    @Query("UPDATE Report r " +
           "   SET r.status = :status, r.processedAt = :now " +
           " WHERE r.category = :category AND r.categoryId = :categoryId")
    int updateStatusByContent(@Param("category")   ReportCategory category,
                              @Param("categoryId") String categoryId,
                              @Param("status")     ReportStatus status,
                              @Param("now")        LocalDateTime now);

	Optional<Report> findFirstByCategoryAndCategoryId(ReportCategory category, String categoryId);

	List<Report> findByCategoryAndCategoryId(ReportCategory category, String categoryId);
}