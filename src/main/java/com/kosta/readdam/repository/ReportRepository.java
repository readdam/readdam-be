package com.kosta.readdam.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.enums.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
	
	@Modifying
    @Query("UPDATE Report r " +
           "   SET r.status = :status, r.processedAt = :now " +
           " WHERE r.category = :category AND r.categoryId = :categoryId")
    int updateStatusByContent(@Param("category")   String category,
                              @Param("categoryId") String categoryId,
                              @Param("status")     ReportStatus status,
                              @Param("now")        LocalDateTime now);
}