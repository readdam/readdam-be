package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.enums.InquiryStatus;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer>, JpaSpecificationExecutor<Inquiry> {

	List<Inquiry> findByUser_UsernameOrderByRegDateDesc(String username);

	@Modifying
	@Query("UPDATE Inquiry i " + "   SET i.answer     = :answer, " + "       i.status     = :status, "
			+ "       i.answerDate = :now " + " WHERE i.inquiryId  = :inquiryId")
	int updateAnswerAndStatus(@Param("inquiryId") Integer inquiryId, @Param("answer") String answer,
			@Param("status") InquiryStatus status, @Param("now") LocalDateTime now);
	
	Page<Inquiry> findByUser_Username(String username, Pageable pageable);
	
}
